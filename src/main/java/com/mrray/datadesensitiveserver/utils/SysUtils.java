package com.mrray.datadesensitiveserver.utils;

import com.mrray.datadesensitiveserver.entity.domain.SuperEntity;
import com.mrray.datadesensitiveserver.entity.dto.PageQueryDto;
import com.mrray.datadesensitiveserver.entity.vo.PageQueryVo;
import com.mrray.datadesensitiveserver.repository.BaseRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;

public final class SysUtils {
    private static Logger logger = LoggerFactory.getLogger("SysUtils");
    private static final String PACKAGE = "com.mrray.datadesensitiveserver.algorithm.";

    private SysUtils() {
    }

    public static void mapperPageInfoToVo(PageQueryDto dto, Page page, PageQueryVo pageVo) {
        pageVo.setPage(dto.getPage());
        pageVo.setSize(dto.getSize());
        pageVo.setFirstPage(page.isFirst());
        pageVo.setLastPage(page.isLast());
        int prevPage = page.isFirst() ? 1 : dto.getPage() - 1;
        pageVo.setPrevPage(prevPage);
        int nextPage = page.isLast() ? page.getTotalPages() : dto.getPage() + 1;
        pageVo.setNextPage(nextPage);
        pageVo.setTotalElements(page.getTotalElements());
        pageVo.setTotalPage(page.getTotalPages());
        pageVo.setCurrentPageElements(page.getNumberOfElements());
        pageVo.setDirection(dto.getDirection());
        pageVo.setProperty(dto.getProperty());
    }

    public static void save(BaseRepository repository, SuperEntity entity) {
        try {
            repository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            entity.setUuid(RandomStringUtils.random(8, true, true).toLowerCase());
            save(repository, entity);
        }
    }

    public static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    public static String encryptBASE64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key).replace("\r\n", "");
    }

    public static Class getClazz(String algorithm) {
        String className = PACKAGE + algorithm;
        logger.info("class name " + className);
        Class aClass = null;
        try {
            aClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
        if (aClass == null) {
            aClass = findInJar(algorithm.replace("Algorithm", "").toLowerCase());
        }
        return aClass;
    }

    private static Class findInJar(String algorithm) {
        String className = PACKAGE + algorithm;
        logger.info("find in jar");
        //docker内路径
        //String path = System.getProperty("user.dir") + "/algorithms";
        //实际路径
        //String path = "/root/data_desens_server/webapps/algorithms";
        String tempJar = createTempJar(algorithm, false);
        logger.info("temp path : " + tempJar);
        if (tempJar == null) {
            return null;
        }
        File file = new File(tempJar);
        logger.info("path " + file.getPath());
        logger.info("exist " + file.exists());
        try (URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, Thread.currentThread().getContextClassLoader())) {
            logger.info("urlClassLoader " + urlClassLoader.getClass());
            Class<?> aClass = urlClassLoader.loadClass(className);
            logger.info("class " + aClass.getName());
            return aClass;
        } catch (Throwable f) {
            logger.error(f.getMessage(), f);
            logger.error("class " + algorithm + " not found");
            return null;
        }
    }

    public static InputStream getJarInputStream(String filePath, String name) throws Exception {
        URL url = new URL("jar:file:" + filePath + "!/" + name);
        JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
        return jarConnection.getInputStream();
    }

    private static byte[] getContent(String filePath) {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        byte[] buffer = new byte[(int) fileSize];
        try (FileInputStream fi = new FileInputStream(file)) {
            int offset = 0;
            int numRead;
            while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }
            // 确保所有数据均被读取
            if (offset != buffer.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return buffer;
    }

    private static void byte2File(byte[] bfile, String filePath, String fileName) {
        File file = new File(filePath + fileName);
        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            File dir = new File(filePath);
            if (!dir.exists() && !dir.isDirectory()) {//判断文件目录是否存在
                boolean mkdirs = dir.mkdirs();
            }
            bos.write(bfile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static String serial() {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("cat /sys/class/dmi/id/product_uuid");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String uuid = br.readLine();
            br.close();
            isr.close();
            is.close();
            if (StringUtils.hasText(uuid)) {
                return DigestUtils.md5Hex(uuid);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String createTempJar(String algorithm, boolean create) {
        String path = System.getProperty("user.dir") + "/algorithms/";
        File file = new File(path + algorithm + "t.alg");
        if (!file.exists() || create) {
            try {
                byte[] content = getContent(path + algorithm + ".alg");
                byte[] bytes = AESEncryption.decrypt(serial(), content);
                //byte[] bytes = AESEncryption.decrypt("E5AADBD5DE864C98A82FC45A77A79AFE", content);
                byte2File(bytes, path, algorithm + "t.alg");
                return file.getPath();
            } catch (Exception e) {
                logger.error(e.getMessage());
                return null;
            }
        } else {
            return file.getPath();
        }
    }

    public static String buildMask(String mask, int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(mask);
        }
        return result.toString();
    }
}