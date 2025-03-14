package com.messenger.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.Logger;

@WebServlet("/api/upload")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
        maxFileSize = 100 * 1024 * 1024,  // 100 MB
        maxRequestSize = 15 * 1024 * 1024 * 10 // 150 MB
)
public class FileUploadServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private static final Logger logger = Logger.getLogger(FileUploadServlet.class.getName());

    @Override
    public void init() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Регистрация модуля для поддержки Java 8 Date/Time
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // Добавьте эту строку!

        String type = request.getParameter("type"); // "photo" или "voice"
        Part filePart = request.getPart("file");
        String fileName = System.currentTimeMillis() + "_" + getSubmittedFileName(filePart);


        // Путь для сохранения
        String uploadPath = getServletContext().getRealPath("/uploads/" + type);
        logger.info("Upload path: " + uploadPath); // Логирование пути
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            logger.info("Directory created: " + created); // Логирование результата создания директории
        }

        try {
            filePart.write(uploadPath + File.separator + fileName);
        } catch (IOException e) {
            System.out.println("Ошибка записи файла: " + e.getMessage());
        }

        // Сохраняем файл
        filePart.write(uploadPath + File.separator + fileName);

        // Возвращаем URL для доступа к файлу
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("url", request.getContextPath() + "/uploads/" + type + "/" + fileName);
        responseMap.put("type", type);

        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), responseMap);
    }

    private String getSubmittedFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                return item.substring(item.indexOf("=") + 2, item.length() - 1);
            }
        }
        return "";
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // Добавьте эту строку!
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}