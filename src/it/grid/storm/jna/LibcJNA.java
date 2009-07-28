package it.grid.storm.jna;

import it.grid.storm.filesystem.swig.system_error;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

public class LibcJNA {

    public interface LibcLibrary extends Library {

        LibcLibrary INSTANCE = (LibcLibrary) Native.loadLibrary(("c"), LibcLibrary.class);

        public static class Group extends Structure {

            public String gr_name;
            public String gr_passwd;
            public int gr_gid;
            public Pointer gr_mem;

        }

        public static class Stat64 extends Structure {

            public NativeLong st_dev;
            public NativeLong st_ino;
            public int st_mode;
            public int st_nlink;
            public int st_uid;
            public int st_gid;
            public NativeLong st_rdev;
            public NativeLong st_size;
            public NativeLong st_atime;
            public NativeLong st_mtime;
            public NativeLong st_ctime;
            public NativeLong st_blksize;
            public NativeLong st_blocks;
            public int st_attr;

        }

        int chown(String fileName, int uid, int gid);

        int stat(String fileName, Stat64 st);

        Group getgrnam(String name);

    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("usage: <fileName>");
            System.exit(1);
        }
        
        String fileName = args[0];
        
        LibcLibrary.Stat64 st = new LibcLibrary.Stat64();

        int ret = LibcLibrary.INSTANCE.stat(fileName, st);
        
        System.out.println("Return status: " + ret);
        
        System.out.println("Size: " + st.st_size);
        

        // LibcLibrary.Group gInfo = LibcLibrary.INSTANCE.getgrnam("storm");

        // System.out.println("name: " + gInfo.gr_name);
        // System.out.println("passwd: " + gInfo.gr_passwd);
        // System.out.println("gid: " + gInfo.gr_gid);

    }
}
