/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package libcore.io;

import android.compat.annotation.UnsupportedAppUsage;
import android.system.ErrnoException;
import android.system.GaiException;
import android.system.Int64Ref;
import android.system.OsConstants;
import android.system.StructAddrinfo;
import android.system.StructLinger;
import android.system.StructMsghdr;
import android.system.StructPollfd;
import android.system.StructStat;
import android.system.StructStatVfs;

import java.io.FileDescriptor;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import dalvik.system.BlockGuard;
import dalvik.system.SocketTagger;

import static android.system.OsConstants.*;

/**
 * Informs BlockGuard of any activity it should be aware of.
 */
public class BlockGuardOs extends ForwardingOs {
    @UnsupportedAppUsage
    public BlockGuardOs(Os os) {
        super(os);
    }

    private FileDescriptor tagSocket(FileDescriptor fd) throws ErrnoException {
        try {
            SocketTagger.get().tag(fd);
            return fd;
        } catch (SocketException e) {
            throw new ErrnoException("socket", EINVAL, e);
        }
    }

    @Override public FileDescriptor accept(FileDescriptor fd, SocketAddress peerAddress) throws ErrnoException, SocketException {
        if (!(isUnixSocket(fd) && isNonBlockingFile(fd))) {
            BlockGuard.getThreadPolicy().onNetwork();
        }
        final FileDescriptor acceptFd = super.accept(fd, peerAddress);
        if (acceptFd != null && isInetSocket(acceptFd)) {
            tagSocket(acceptFd);
        }
        return acceptFd;
    }

    @Override public boolean access(String path, int mode) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        return super.access(path, mode);
    }

    @UnsupportedAppUsage
    @Override public void chmod(String path, int mode) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        super.chmod(path, mode);
    }

    @UnsupportedAppUsage
    @Override public void chown(String path, int uid, int gid) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        super.chown(path, uid, gid);
    }

    @UnsupportedAppUsage
    @Override public void close(FileDescriptor fd) throws ErrnoException {
        try {
            // The usual case is that this _isn't_ a socket, so the getsockopt(2) call in
            // isLingerSocket will throw, and that's really expensive. Try to avoid asking
            // if we don't care.
            if (fd.isSocket$()) {
                if (isLingerSocket(fd)) {
                    // If the fd is a socket with SO_LINGER set, we might block indefinitely.
                    // We allow non-linger sockets so that apps can close their network
                    // connections in methods like onDestroy which will run on the UI thread.
                    BlockGuard.getThreadPolicy().onNetwork();
                }
            }
        } catch (ErrnoException ignored) {
            // We're called via Socket.close (which doesn't ask for us to be called), so we
            // must not throw here, because Socket.close must not throw if asked to close an
            // already-closed socket. Also, the passed-in FileDescriptor isn't necessarily
            // a socket at all.
        }
        super.close(fd);
    }

    public static boolean isNonBlockingFile(FileDescriptor fd) throws ErrnoException {
        int flag = android.system.Os.fcntlInt(fd, F_GETFL, 0);
        if ((flag & O_NONBLOCK) != 0) {
            return true;
        }
        return false;
    }

    public static boolean isUnixSocket(FileDescriptor fd) throws ErrnoException {
        return isUnixDomain(Libcore.os.getsockoptInt(fd, SOL_SOCKET, SO_DOMAIN));
    }

    private static boolean isUnixDomain(int domain) {
        return (domain == AF_UNIX);
    }

    private static boolean isInetSocket(FileDescriptor fd) throws ErrnoException{
        return isInetDomain(Libcore.os.getsockoptInt(fd, SOL_SOCKET, SO_DOMAIN));
    }

    private static boolean isInetDomain(int domain) {
        return (domain == AF_INET) || (domain == AF_INET6);
    }

    @SuppressWarnings("NewApi") // False positive lint limitation, see b/177434707.
    private static boolean isLingerSocket(FileDescriptor fd) throws ErrnoException {
        StructLinger linger = Libcore.os.getsockoptLinger(fd, SOL_SOCKET, SO_LINGER);
        return linger.isOn() && linger.l_linger > 0;
    }

    private static boolean isUdpSocket(FileDescriptor fd) throws ErrnoException {
        return Libcore.os.getsockoptInt(fd, SOL_SOCKET, SO_PROTOCOL) == IPPROTO_UDP;
    }

    @Override public void connect(FileDescriptor fd, InetAddress address, int port)
            throws ErrnoException, SocketException {
        boolean skipGuard = false;
        try {
            skipGuard = isUdpSocket(fd);
        } catch (ErrnoException ignored) {
        }
        if (!skipGuard) BlockGuard.getThreadPolicy().onNetwork();
        super.connect(fd, address, port);
    }

    @Override public void connect(FileDescriptor fd, SocketAddress address) throws ErrnoException,
            SocketException {
        boolean skipGuard = false;
        try {
            skipGuard = isUdpSocket(fd);
        } catch (ErrnoException ignored) {
        }
        if (!skipGuard) BlockGuard.getThreadPolicy().onNetwork();
        super.connect(fd, address);
    }

    @UnsupportedAppUsage
    @Override public void fchmod(FileDescriptor fd, int mode) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        super.fchmod(fd, mode);
    }

    @UnsupportedAppUsage
    @Override public void fchown(FileDescriptor fd, int uid, int gid) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        super.fchown(fd, uid, gid);
    }

    // TODO: Untag newFd when needed for dup2(FileDescriptor oldFd, int newFd)

    @UnsupportedAppUsage
    @Override public void fdatasync(FileDescriptor fd) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        super.fdatasync(fd);
    }

    @UnsupportedAppUsage
    @Override public StructStat fstat(FileDescriptor fd) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        return super.fstat(fd);
    }

    @UnsupportedAppUsage
    @Override public StructStatVfs fstatvfs(FileDescriptor fd) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        return super.fstatvfs(fd);
    }

    @Override public void fsync(FileDescriptor fd) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        super.fsync(fd);
    }

    @Override public void ftruncate(FileDescriptor fd, long length) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        super.ftruncate(fd, length);
    }

    @Override public InetAddress[] android_getaddrinfo(String node, StructAddrinfo hints, int netId) throws GaiException {
        // With AI_NUMERICHOST flag set, the node must a numerical network address, therefore no
        // host address lookups will be performed. In this case, it is fine to perform on main
        // thread.
        boolean isNumericHost = (hints.ai_flags & AI_NUMERICHOST) != 0;
        if (!isNumericHost) {
            BlockGuard.getThreadPolicy().onNetwork();
        }
        return super.android_getaddrinfo(node, hints, netId);
    }

    @UnsupportedAppUsage
    @Override public void lchown(String path, int uid, int gid) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        super.lchown(path, uid, gid);
    }

    @UnsupportedAppUsage
    @Override public void link(String oldPath, String newPath) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        BlockGuard.getVmPolicy().onPathAccess(oldPath);
        BlockGuard.getVmPolicy().onPathAccess(newPath);
        super.link(oldPath, newPath);
    }

    @UnsupportedAppUsage
    @Override public long lseek(FileDescriptor fd, long offset, int whence) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        return super.lseek(fd, offset, whence);
    }

    @UnsupportedAppUsage
    @Override public StructStat lstat(String path) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        return super.lstat(path);
    }

    @UnsupportedAppUsage
    @Override public void mkdir(String path, int mode) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        super.mkdir(path, mode);
    }

    @UnsupportedAppUsage
    @Override public void mkfifo(String path, int mode) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        super.mkfifo(path, mode);
    }

    @UnsupportedAppUsage
    @Override public FileDescriptor open(String path, int flags, int mode) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        if ((flags & O_ACCMODE) != O_RDONLY) {
            BlockGuard.getThreadPolicy().onWriteToDisk();
        }
        return super.open(path, flags, mode);
    }

    @Override public int poll(StructPollfd[] fds, int timeoutMs) throws ErrnoException {
        // Greater than 0 is a timeout in milliseconds and -1 means "block forever",
        // but 0 means "poll and return immediately", which shouldn't be subject to BlockGuard.
        if (timeoutMs != 0) {
            BlockGuard.getThreadPolicy().onNetwork();
        }
        return super.poll(fds, timeoutMs);
    }

    @UnsupportedAppUsage
    @Override public void posix_fallocate(FileDescriptor fd, long offset, long length) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        super.posix_fallocate(fd, offset, length);
    }

    @UnsupportedAppUsage
    @Override public int pread(FileDescriptor fd, ByteBuffer buffer, long offset) throws ErrnoException, InterruptedIOException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        return super.pread(fd, buffer, offset);
    }

    @UnsupportedAppUsage
    @Override public int pread(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, long offset) throws ErrnoException, InterruptedIOException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        return super.pread(fd, bytes, byteOffset, byteCount, offset);
    }

    @UnsupportedAppUsage
    @Override public int pwrite(FileDescriptor fd, ByteBuffer buffer, long offset) throws ErrnoException, InterruptedIOException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        return super.pwrite(fd, buffer, offset);
    }

    @UnsupportedAppUsage
    @Override public int pwrite(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, long offset) throws ErrnoException, InterruptedIOException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        return super.pwrite(fd, bytes, byteOffset, byteCount, offset);
    }

    @UnsupportedAppUsage
    @Override public int read(FileDescriptor fd, ByteBuffer buffer) throws ErrnoException, InterruptedIOException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        return super.read(fd, buffer);
    }

    @UnsupportedAppUsage
    @Override public int read(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount) throws ErrnoException, InterruptedIOException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        return super.read(fd, bytes, byteOffset, byteCount);
    }

    @UnsupportedAppUsage
    @Override public String readlink(String path) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        return super.readlink(path);
    }

    @UnsupportedAppUsage
    @Override public String realpath(String path) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        return super.realpath(path);
    }

    @UnsupportedAppUsage
    @Override public int readv(FileDescriptor fd, Object[] buffers, int[] offsets, int[] byteCounts) throws ErrnoException, InterruptedIOException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        return super.readv(fd, buffers, offsets, byteCounts);
    }

    @Override public int recvfrom(FileDescriptor fd, ByteBuffer buffer, int flags, InetSocketAddress srcAddress) throws ErrnoException, SocketException {
        BlockGuard.getThreadPolicy().onNetwork();
        return super.recvfrom(fd, buffer, flags, srcAddress);
    }

    @Override public int recvfrom(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, int flags, InetSocketAddress srcAddress) throws ErrnoException, SocketException {
        BlockGuard.getThreadPolicy().onNetwork();
        return super.recvfrom(fd, bytes, byteOffset, byteCount, flags, srcAddress);
    }

    @Override public int recvmsg(FileDescriptor fd, StructMsghdr msg, int flags) throws ErrnoException, SocketException {
        BlockGuard.getThreadPolicy().onNetwork();
        return super.recvmsg(fd, msg, flags);
    }

    @UnsupportedAppUsage
    @Override public void remove(String path) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        super.remove(path);
    }

    @UnsupportedAppUsage
    @Override public void rename(String oldPath, String newPath) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        BlockGuard.getVmPolicy().onPathAccess(oldPath);
        BlockGuard.getVmPolicy().onPathAccess(newPath);
        super.rename(oldPath, newPath);
    }

    @Override public long sendfile(FileDescriptor outFd, FileDescriptor inFd, Int64Ref offset, long byteCount) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        return super.sendfile(outFd, inFd, offset, byteCount);
    }

    @Override public int sendmsg(FileDescriptor fd, StructMsghdr msg, int flags) throws ErrnoException, SocketException {
        BlockGuard.getThreadPolicy().onNetwork();
        return super.sendmsg(fd, msg, flags);
    }

    @Override public int sendto(FileDescriptor fd, ByteBuffer buffer, int flags, InetAddress inetAddress, int port) throws ErrnoException, SocketException {
        BlockGuard.getThreadPolicy().onNetwork();
        return super.sendto(fd, buffer, flags, inetAddress, port);
    }

    @Override public int sendto(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, int flags, InetAddress inetAddress, int port) throws ErrnoException, SocketException {
        // We permit datagrams without hostname lookups.
        if (inetAddress != null) {
            BlockGuard.getThreadPolicy().onNetwork();
        }
        return super.sendto(fd, bytes, byteOffset, byteCount, flags, inetAddress, port);
    }

    @Override public FileDescriptor socket(int domain, int type, int protocol) throws ErrnoException {
        final FileDescriptor fd = super.socket(domain, type, protocol);
        if (isInetDomain(domain)) {
            tagSocket(fd);
        }
        return fd;
    }

    @Override public void socketpair(int domain, int type, int protocol, FileDescriptor fd1, FileDescriptor fd2) throws ErrnoException {
        super.socketpair(domain, type, protocol, fd1, fd2);
        if (isInetDomain(domain)) {
            tagSocket(fd1);
            tagSocket(fd2);
        }
    }

    @UnsupportedAppUsage
    @Override public StructStat stat(String path) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        return super.stat(path);
    }

    @UnsupportedAppUsage
    @Override public StructStatVfs statvfs(String path) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        return super.statvfs(path);
    }

    @UnsupportedAppUsage
    @Override public void symlink(String oldPath, String newPath) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        BlockGuard.getVmPolicy().onPathAccess(oldPath);
        BlockGuard.getVmPolicy().onPathAccess(newPath);
        super.symlink(oldPath, newPath);
    }

    @UnsupportedAppUsage
    @Override public int write(FileDescriptor fd, ByteBuffer buffer) throws ErrnoException, InterruptedIOException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        return super.write(fd, buffer);
    }

    @UnsupportedAppUsage
    @Override public int write(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount) throws ErrnoException, InterruptedIOException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        return super.write(fd, bytes, byteOffset, byteCount);
    }

    @UnsupportedAppUsage
    @Override public int writev(FileDescriptor fd, Object[] buffers, int[] offsets, int[] byteCounts) throws ErrnoException, InterruptedIOException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        return super.writev(fd, buffers, offsets, byteCounts);
    }

    @Override public void execv(String filename, String[] argv) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        BlockGuard.getVmPolicy().onPathAccess(filename);
        super.execv(filename, argv);
    }

    @Override public void execve(String filename, String[] argv, String[] envp)
            throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        BlockGuard.getVmPolicy().onPathAccess(filename);
        super.execve(filename, argv, envp);
    }

    @Override public byte[] getxattr(String path, String name) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        return super.getxattr(path, name);
    }

    @Override public void msync(long address, long byteCount, int flags) throws ErrnoException {
        if ((flags & OsConstants.MS_SYNC) != 0) {
            BlockGuard.getThreadPolicy().onWriteToDisk();
        }
        super.msync(address, byteCount, flags);
    }

    @Override public void removexattr(String path, String name) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        super.removexattr(path, name);
    }

    @Override public void setxattr(String path, String name, byte[] value, int flags)
            throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        BlockGuard.getVmPolicy().onPathAccess(path);
        super.setxattr(path, name, value, flags);
    }

    @Override public int sendto(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount,
                                int flags, SocketAddress address) throws ErrnoException, SocketException {
        BlockGuard.getThreadPolicy().onNetwork();
        return super.sendto(fd, bytes, byteOffset, byteCount, flags, address);
    }

    @Override public void unlink(String pathname) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        BlockGuard.getVmPolicy().onPathAccess(pathname);
        super.unlink(pathname);
    }

    @Override public long splice(FileDescriptor fdIn, Int64Ref offIn, FileDescriptor fdOut, Int64Ref offOut, long len, int flags) throws ErrnoException {
        // It's infeasible to figure out if splice will result in read or write (would require fstat to figure out which fd is pipe).
        // So, signal both read and write.
        BlockGuard.getThreadPolicy().onWriteToDisk();
        BlockGuard.getThreadPolicy().onReadFromDisk();
        return super.splice(fdIn, offIn, fdOut, offOut, len, flags);
    }
}