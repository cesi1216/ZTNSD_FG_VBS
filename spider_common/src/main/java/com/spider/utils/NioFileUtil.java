package com.spider.utils;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class NioFileUtil {


    /**
     * 重命名文件/文件夹
     * @param filePath 源文件地址
     * @param reName 新名称
     *
     * @throws IOException
     * @see {@link #operateDir(boolean, Path, Path, CopyOption...)}
     */
    public static void reNameFile(String filePath,String reName) throws IOException{
            File file = new File(filePath);
            String path = "";
            int i = filePath.lastIndexOf(File.separator);
            path = filePath.substring(0,i+1);

            file.renameTo(new File(path+reName));
    }
    /**
     * 移动文件
     * @param source D:\IDEAworkspace\201910_ztrsd\spider_vbs\spider_VBS\spider_web\target\spider_web\resources\tmptxt\difang001s003.txt
     * @param target E:\difang001s003.txt
     * @param options
     * @throws IOException
     * @see {@link #operateDir(boolean, Path, Path, CopyOption...)}
     */
    public static void moveFile(Path source, Path target, CopyOption... options) throws IOException{
        Files.move(source, target,options);
    }
    /**
     * 复制文件夹
     * @param source
     * @param target
     * @param options
     * @throws IOException
     * @see {@link #operateDir(boolean, Path, Path, CopyOption...)}
     */
    public static void copyDir(Path source, Path target, CopyOption... options) throws IOException{
        operateDir(false, source, target, options);
    }
    /**
     * 移动文件夹
     * @param source
     * @param target
     * @param options
     * @throws IOException
     * @see {@link #operateDir(boolean, Path, Path, CopyOption...)}
     */
    public static void moveDir(Path source, Path target, CopyOption... options) throws IOException{
        operateDir(true, source, target, options);
    }
    /**
     * 复制/移动文件夹
     * @param move 操作标记，为true时移动文件夹,否则为复制
     * @param source 要复制/移动的源文件夹
     * @param target 源文件夹要复制/移动到的目标文件夹
     * @param options 文件复制选项
     * @throws IOException
     * @see Files#move(Path, Path, CopyOption...)
     * @see Files#copy(Path, Path, CopyOption...)
     * @see Files#walkFileTree(Path, java.nio.file.FileVisitor)
     */
    public static void operateDir(final boolean move, final Path source, Path target, final CopyOption... options) throws IOException{
        if(null==source||!Files.isDirectory(source))
            throw new IllegalArgumentException("source must be directory");
        final Path dest = target.resolve(source.getFileName());
        // 如果相同则返回
        if(Files.exists(dest)&&Files.isSameFile(source, dest))return;
        // 目标文件夹不能是源文件夹的子文件夹
        // isSub方法实现参见另一篇博客 http://blog.csdn.net/10km/article/details/54425614
        if(isSub(source,dest))
            throw new IllegalArgumentException("dest must not  be sub directory of source");
        boolean clear=true;
        for(CopyOption option:options)
            if(StandardCopyOption.REPLACE_EXISTING==option){
                clear=false;
                break;
            }
        // 如果指定了REPLACE_EXISTING选项则不清除目标文件夹
        if(clear)
            deleteIfExists(dest);
        Files.walkFileTree(source,
                new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        // 在目标文件夹中创建dir对应的子文件夹
                        Path subDir = 0==dir.compareTo(source)?dest:dest.resolve(dir.subpath(source.getNameCount(), dir.getNameCount()));
                        Files.createDirectories(subDir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if(move)
                            Files.move(file, dest.resolve(file.subpath(source.getNameCount(), file.getNameCount())),options);
                        else
                            Files.copy(file, dest.resolve(file.subpath(source.getNameCount(), file.getNameCount())),options);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        // 移动操作时删除源文件夹
                        if(move)
                            Files.delete(dir);
                        return super.postVisitDirectory(dir, exc);
                    }
                });
    }

    /**
     * 强制删除文件/文件夹(含不为空的文件夹)<br>
     * @param dir
     * @throws IOException
     * @see Files#deleteIfExists(Path)
     * @see Files#walkFileTree(Path, java.nio.file.FileVisitor)
     */
    public static void deleteIfExists(Path dir) throws IOException {
        try {
            Files.deleteIfExists(dir);
        } catch (DirectoryNotEmptyException e) {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return super.postVisitDirectory(dir, exc);
                }
            });
        }
    }

    /**
     * 判断sub是否与parent相等或在其之下<br>
     * parent必须存在，且必须是directory,否则抛出{@link IllegalArgumentException}
     * @param parent
     * @param sub
     * @return
     * @throws IOException
     */
    public static boolean sameOrSub(Path parent,Path sub) throws IOException {
        if(null==parent)
            throw new NullPointerException("parent is null");
        if(!Files.exists(parent)||!Files.isDirectory(parent))
            throw new IllegalArgumentException(String.format("the parent not exist or not directory %s",parent));
        while(null!=sub) {
            if(Files.exists(sub)&&Files.isSameFile(parent, sub))
                return true;
            sub=sub.getParent();
        }
        return false;
    }
    /**
     * 判断sub是否在parent之下的文件或子文件夹<br>
     * parent必须存在，且必须是directory,否则抛出{@link IllegalArgumentException}
     * @param parent
     * @param sub
     * @return
     * @throws IOException
     * @see {@link #sameOrSub(Path, Path)}
     */
    public static boolean isSub(Path parent,Path sub) throws IOException{
        return (null==sub)?false:sameOrSub(parent,sub.getParent());
    }


    //利用系统指令删除文件夹
    public static String forceDeleteDirectory_linux(String directory){
        File tagFile=new File(directory);
        if(tagFile.exists()){
            try {

                String[] cmd = new String[] {"sh", "-c", "rm -f -r "+directory};
                Runtime rt = Runtime.getRuntime(); // 获取运行时系统
                Process proc = rt.exec(cmd); // 执行命令
                InputStream stderr =  proc.getInputStream(); // 获取输入流
                InputStreamReader isr = new InputStreamReader(stderr,"gbk");
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                /*while ((line = br.readLine()) != null) { // 打印出命令执行的结果
                System.out.println(line);
            }*/
            } catch (Throwable t) {
                t.printStackTrace();
                return t.getMessage();
            }
            return "ok";
        }else {
            return "目录不存在";
        }
    }
    //利用系统指令删除文件夹
    public static String forceDeleteDirectory_windows(String directory){
        File tagFile=new File(directory);
        if(tagFile.exists()){
            try {

                String[] cmd = new String[] {"cmd.exe", "/c", "rd "+directory+" /q /s"};
                Runtime rt = Runtime.getRuntime(); // 获取运行时系统
                Process proc = rt.exec(cmd); // 执行命令
                InputStream stderr =  proc.getInputStream(); // 获取输入流
                InputStreamReader isr = new InputStreamReader(stderr,"gbk");
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                /*while ((line = br.readLine()) != null) { // 打印出命令执行的结果
                System.out.println(line);
            }*/
            } catch (Throwable t) {
                t.printStackTrace();
                return t.getMessage();
            }
            return "ok";
        }else {
            return "目录不存在";
        }
    }
}
