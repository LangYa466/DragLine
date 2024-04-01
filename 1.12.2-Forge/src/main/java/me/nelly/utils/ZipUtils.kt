package me.nelly.utils;// 引入包
import org.slf4j.LoggerFactory
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.util.zip.ZipInputStream

// 工具类
object ZipUtils {
    var log = LoggerFactory.getLogger(ZipUtils::class.java)

    fun unzip(zipFilePath: String, desDirectory: String) {
        var desDir = File(desDirectory)
        if (!desDir.exists()) {
            var mkdirSuccess = desDir.mkdir();
            if (!mkdirSuccess) {
                throw Exception("创建解压目标文件夹失败")
            }
        }
        // 读入流（第二个参数，处理压缩文件中文异常。如果没有中文，可以不写第二个参数）
        var zipInputStream = ZipInputStream(FileInputStream(zipFilePath), Charset.forName("GBK"))
        // 遍历每一个文件
        var zipEntry = zipInputStream.nextEntry
        while (zipEntry != null) {
            log.info(zipEntry.toString())
            var unzipFilePath = desDirectory + File.separator + zipEntry.name
            if (zipEntry.isDirectory) {
                // 直接创建（注意，不是使用系统的mkdir,自定义方法）
                mkdir(File(unzipFilePath))
            } else {
                var file = File(unzipFilePath)
                // 创建父目录（注意，不是使用系统的mkdir,自定义方法）
                mkdir(file.parentFile)
                // 写出文件
                var bufferedOutputStream = BufferedOutputStream(FileOutputStream(unzipFilePath))
                val bytes = ByteArray(1024)
                var readLen: Int
                // Java 与 Kotlin的不同之处，需要特别关注。
                // while ((readLen = zipInputStream.read(bytes))!=-1){
                while (zipInputStream.read(bytes).also { readLen = it } > 0) {
                    bufferedOutputStream.write(bytes, 0, readLen)
                }
                bufferedOutputStream.close()
            }
            zipInputStream.closeEntry()
            zipEntry = zipInputStream.nextEntry
        }
    }

    /**
     * 创建文件夹
     */
    private fun mkdir(file: File) {
        if (file.exists()) {
            return
        } else {
            file.parentFile.mkdir()
            file.mkdir()
        }
    }


}