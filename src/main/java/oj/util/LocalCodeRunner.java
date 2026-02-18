package oj.util;

import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class LocalCodeRunner {
    // 修改为平台无关的临时目录路径
    private static final String TEMP_PATH = System.getProperty("java.io.tmpdir") + "code/";
    // 超时阈值（毫秒）
    private static final long TIMEOUT_MS = 10000;
    // 检测是否为Windows系统
    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");

    public LocalCodeRunner() {
        // 创建临时目录
        java.io.File tempDir = new java.io.File(TEMP_PATH);
        if (!tempDir.exists()) {
            boolean created = tempDir.mkdirs();
            System.out.println("创建临时目录: " + TEMP_PATH + " - " + (created ? "成功" : "失败"));
        }
    }

    public String runLocalCode(String code, String language, String testInput) {
        try {
            System.out.println("开始运行代码：语言=" + language + ", 代码长度=" + code.length());
            String fileName = UUID.randomUUID().toString();
            String fullPath = TEMP_PATH + fileName;

            switch (language.toLowerCase()) {
                case "cpp":
                    fullPath += ".cpp";
                    writeCodeToFile(fullPath, code);
                    return runCpp(fullPath, testInput);
                case "java":
                    fullPath += ".java";
                    String javaCode = code.replaceAll("public class \\w+", "public class " + fileName);
                    writeCodeToFile(fullPath, javaCode);
                    return runJava(fullPath, testInput, fileName);
                case "python":
                    fullPath += ".py";
                    writeCodeToFile(fullPath, code);
                    return runPython(fullPath, testInput);
                default:
                    return "不支持的语言：" + language;
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈，方便调试
            return "运行失败：" + e.getMessage();
        }
    }

    private void writeCodeToFile(String path, String code) throws Exception {
        try (java.io.FileWriter writer = new java.io.FileWriter(path)) {
            writer.write(code);
            System.out.println("成功写入代码到：" + path);
        }
    }

    // C++代码运行逻辑
    private String runCpp(String cppPath, String input) throws Exception {
        Process compileProcess = null;
        Process runProcess = null;
        String executablePath = cppPath.replace(".cpp", IS_WINDOWS ? ".exe" : "");

        try {
            // 编译C++代码
            String compileCmd = IS_WINDOWS ? "g++" : "g++"; // 假设Windows上也安装了MinGW或Cygwin
            ProcessBuilder compilePB = new ProcessBuilder(compileCmd, cppPath, "-o", executablePath);
            compilePB.redirectErrorStream(true);
            compilePB.directory(new java.io.File(TEMP_PATH));

            compileProcess = compilePB.start();
            boolean compileFinished = compileProcess.waitFor(TIMEOUT_MS, TimeUnit.MILLISECONDS);

            if (!compileFinished) {
                compileProcess.destroy();
                return "编译超时（超过" + TIMEOUT_MS + "ms）";
            }

            int exitCode = compileProcess.exitValue();
            if (exitCode != 0) {
                String errorOutput = readStream(compileProcess.getInputStream());
                return "编译失败：" + errorOutput;
            }

            // 运行编译后的程序
            System.out.println("执行C++程序: " + executablePath);
            ProcessBuilder runPB = new ProcessBuilder(IS_WINDOWS ? executablePath : "./" + executablePath);
            runPB.redirectErrorStream(true);
            runPB.directory(new java.io.File(TEMP_PATH));

            runProcess = runPB.start();

            // 写入输入
            try (java.io.OutputStream os = runProcess.getOutputStream()) {
                os.write(input.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close(); // 立即关闭输入流
            }

            // 超时检测
            boolean runFinished = runProcess.waitFor(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!runFinished) {
                runProcess.destroy();
                return "运行超时（超过" + TIMEOUT_MS + "ms）";
            }

            // 读取输出
            String fullOutput = readStream(runProcess.getInputStream()).trim();
            System.out.println("C++运行结果：输入=" + input + "，输出=" + fullOutput);

            if (fullOutput.isEmpty()) {
                return "运行成功，输出：";
            }
            return "运行成功，输出：" + fullOutput;

        } finally {
            // 清理进程和文件
            if (compileProcess != null) compileProcess.destroy();
            if (runProcess != null) runProcess.destroy();

            // 延迟删除文件
            try {
                Thread.sleep(100);
                new java.io.File(cppPath).delete();
                new java.io.File(executablePath).delete();
            } catch (Exception e) {
                System.err.println("删除C++文件失败：" + e.getMessage());
            }
        }
    }

    // Java代码运行逻辑
    private String runJava(String javaPath, String input, String className) throws Exception {
        Process compileProcess = null;
        Process runProcess = null;

        try {
            // 编译Java代码
            System.out.println("编译Java代码: " + javaPath);
            ProcessBuilder compilePB = new ProcessBuilder("javac", javaPath);
            compilePB.redirectErrorStream(true);
            compilePB.directory(new java.io.File(TEMP_PATH));

            compileProcess = compilePB.start();
            boolean compileFinished = compileProcess.waitFor(TIMEOUT_MS, TimeUnit.MILLISECONDS);

            if (!compileFinished) {
                compileProcess.destroy();
                return "编译超时（超过" + TIMEOUT_MS + "ms）";
            }

            int exitCode = compileProcess.exitValue();
            if (exitCode != 0) {
                String errorOutput = readStream(compileProcess.getInputStream());
                return "编译失败：" + errorOutput;
            }

            // 运行Java程序
            System.out.println("执行Java程序: " + className);
            ProcessBuilder runPB = new ProcessBuilder("java", "-cp", TEMP_PATH, className);
            runPB.redirectErrorStream(true);
            runPB.directory(new java.io.File(TEMP_PATH));

            runProcess = runPB.start();

            // 写入输入
            try (java.io.OutputStream os = runProcess.getOutputStream()) {
                os.write(input.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close(); // 立即关闭输入流
            }

            // 超时检测
            boolean runFinished = runProcess.waitFor(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!runFinished) {
                runProcess.destroy();
                return "运行超时（超过" + TIMEOUT_MS + "ms）";
            }

            // 读取输出
            String fullOutput = readStream(runProcess.getInputStream()).trim();
            System.out.println("Java运行结果：输入=" + input + "，输出=" + fullOutput);

            if (fullOutput.isEmpty()) {
                return "运行成功，输出：";
            }
            return "运行成功，输出：" + fullOutput;

        } finally {
            // 清理进程和文件
            if (compileProcess != null) compileProcess.destroy();
            if (runProcess != null) runProcess.destroy();

            // 延迟删除文件
            try {
                Thread.sleep(100);
                new java.io.File(javaPath).delete();
                new java.io.File(TEMP_PATH + className + ".class").delete();
            } catch (Exception e) {
                System.err.println("删除Java文件失败：" + e.getMessage());
            }
        }
    }

    // 修复后的Python运行逻辑
    private String runPython(String pyPath, String input) throws Exception {
        Process runProcess = null;
        try {
            // 根据操作系统选择Python命令
            String pythonCmd = IS_WINDOWS ? "python" : "python3";
            System.out.println("执行Python命令: " + pythonCmd + " " + pyPath);

            ProcessBuilder pb = new ProcessBuilder(pythonCmd, pyPath);
            pb.redirectErrorStream(true); // 合并标准输出和错误输出

            // 设置工作目录为临时目录
            pb.directory(new java.io.File(TEMP_PATH));

            runProcess = pb.start();

            // 写入输入
            try (java.io.OutputStream os = runProcess.getOutputStream()) {
                os.write(input.getBytes(StandardCharsets.UTF_8));
                os.flush();
                // 立即关闭输入流，避免Python等待更多输入
                os.close();
            }

            // 超时检测
            boolean runFinished = runProcess.waitFor(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!runFinished) {
                runProcess.destroy();
                return "运行超时（超过" + TIMEOUT_MS + "ms）";
            }

            // 读取所有输出
            String fullOutput = readStream(runProcess.getInputStream()).trim();
            System.out.println("Python运行结果：输入=" + input + "，输出=" + fullOutput);

            // 无输出时返回空字符串而不是0
            if (fullOutput.isEmpty()) {
                return "运行成功，输出：";
            }
            return "运行成功，输出：" + fullOutput;

        } finally {
            if (runProcess != null) runProcess.destroy();
            // 延迟删除文件，确保读取完成
            try {
                Thread.sleep(100);
                new java.io.File(pyPath).delete();
            } catch (Exception e) {
                System.err.println("删除Python文件失败：" + e.getMessage());
            }
        }
    }

    // 读取流的通用方法
    private String readStream(InputStream stream) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString().trim();
        }
    }
}



//package oj.util;
//
//import org.springframework.stereotype.Component;
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class LocalCodeRunner {
//    // 修改为平台无关的临时目录路径
//    private static final String TEMP_PATH = System.getProperty("java.io.tmpdir") + "code/";
//    // 超时阈值（毫秒）
//    private static final long TIMEOUT_MS = 10000;
//    // 检测是否为Windows系统
//    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
//
//    public LocalCodeRunner() {
//        // 创建临时目录
//        java.io.File tempDir = new java.io.File(TEMP_PATH);
//        if (!tempDir.exists()) {
//            boolean created = tempDir.mkdirs();
//            System.out.println("创建临时目录: " + TEMP_PATH + " - " + (created ? "成功" : "失败"));
//        }
//    }
//
//    public String runLocalCode(String code, String language, String testInput) {
//        try {
//            System.out.println("开始运行代码：语言=" + language + ", 代码长度=" + code.length());
//            String fileName = UUID.randomUUID().toString();
//            // 使用File.separator确保路径分隔符正确
//            String fullPath = TEMP_PATH + java.io.File.separator + fileName;
//
//            switch (language.toLowerCase()) {
//                case "cpp":
//                    fullPath += ".cpp";
//                    writeCodeToFile(fullPath, code);
//                    return runCpp(fullPath, testInput);
//                case "java":
//                    fullPath += ".java";
//                    String javaCode = code.replaceAll("public class \\w+", "public class " + fileName);
//                    writeCodeToFile(fullPath, javaCode);
//                    return runJava(fullPath, testInput, fileName);
//                case "python":
//                    fullPath += ".py";
//                    writeCodeToFile(fullPath, code);
//                    return runPython(fullPath, testInput);
//                default:
//                    return "不支持的语言：" + language;
//            }
//        } catch (Exception e) {
//            e.printStackTrace(); // 打印异常堆栈，方便调试
//            return "运行失败：" + e.getMessage();
//        }
//    }
//
//    private void writeCodeToFile(String path, String code) throws Exception {
//        try (java.io.FileWriter writer = new java.io.FileWriter(path)) {
//            writer.write(code);
//            System.out.println("成功写入代码到：" + path);
//        }
//    }
//
//    // C++代码运行逻辑 - 改进版本
//// ... existing code ...
//    // C++代码运行逻辑 - 改进版本
//    private String runCpp(String cppPath, String input) throws Exception {
//        Process compileProcess = null;
//        Process runProcess = null;
//        // 使用平台相关的可执行文件扩展名
//        String executablePath = cppPath.replace(".cpp", IS_WINDOWS ? ".exe" : "");
//
//        try {
//            // 添加更详细的日志
//            System.out.println("=== C++代码执行开始 ===");
//            System.out.println("源文件路径: " + cppPath);
//            System.out.println("可执行文件路径: " + executablePath);
//            System.out.println("输入内容: " + input);
//
//            // 在Windows上查找合适的C++编译器
//            String compileCmd = findCppCompiler();
//            if (compileCmd == null) {
//                System.err.println("错误：未找到C++编译器");
//                return "错误：未找到C++编译器，请确保已安装MinGW或Visual Studio并添加到系统PATH中";
//            }
//
//            System.out.println("使用C++编译器: " + compileCmd);
//
//            // 编译C++代码
//            ProcessBuilder compilePB = new ProcessBuilder(compileCmd, cppPath, "-o", executablePath);
//            compilePB.redirectErrorStream(true);
//            compilePB.directory(new java.io.File(TEMP_PATH));
//
//            try {
//                System.out.println("开始编译C++代码...");
//                compileProcess = compilePB.start();
//            } catch (Exception e) {
//                System.err.println("编译命令执行失败：" + e.getMessage());
//                e.printStackTrace();
//                return "编译命令执行失败：" + e.getMessage() + "。请检查编译器是否正确安装并添加到PATH中";
//            }
//
//            boolean compileFinished = compileProcess.waitFor(TIMEOUT_MS, TimeUnit.MILLISECONDS);
//
//            if (!compileFinished) {
//                compileProcess.destroy();
//                System.err.println("编译超时");
//                return "编译超时（超过" + TIMEOUT_MS + "ms）";
//            }
//
//            int exitCode = compileProcess.exitValue();
//            if (exitCode != 0) {
//                String errorOutput = readStream(compileProcess.getInputStream());
//                System.err.println("编译失败，退出码: " + exitCode + ", 错误输出: " + errorOutput);
//                return "编译失败：" + errorOutput;
//            } else {
//                // 即使编译成功也读取输出，可能有警告信息
//                String compileOutput = readStream(compileProcess.getInputStream());
//                if (!compileOutput.isEmpty()) {
//                    System.out.println("编译输出: " + compileOutput);
//                }
//            }
//
//            // 检查可执行文件是否生成成功
//            java.io.File exeFile = new java.io.File(executablePath);
//            if (!exeFile.exists()) {
//                System.err.println("编译成功但未生成可执行文件");
//                return "编译命令执行成功，但未生成可执行文件，请检查编译输出";
//            }
//            System.out.println("编译成功，可执行文件大小: " + exeFile.length() + " bytes");
//
//            // 运行编译后的程序
//            System.out.println("执行C++程序: " + executablePath);
//            ProcessBuilder runPB;
//
//            if (IS_WINDOWS) {
//                // 在Windows上直接运行可执行文件
//                runPB = new ProcessBuilder(executablePath);
//            } else {
//                // 在Linux/Mac上使用./运行可执行文件
//                runPB = new ProcessBuilder("./" + executablePath);
//            }
//
//            runPB.redirectErrorStream(true);
//            runPB.directory(new java.io.File(TEMP_PATH));
//
//            try {
//                runProcess = runPB.start();
//            } catch (Exception e) {
//                System.err.println("运行程序失败：" + e.getMessage());
//                e.printStackTrace();
//                return "运行程序失败：" + e.getMessage();
//            }
//
//            // 写入输入
//            try (java.io.OutputStream os = runProcess.getOutputStream()) {
//                System.out.println("写入输入到程序...");
//                os.write(input.getBytes(StandardCharsets.UTF_8));
//                os.flush();
//                os.close(); // 立即关闭输入流
//                System.out.println("输入写入完成并关闭流");
//            }
//
//            // 超时检测
//            boolean runFinished = runProcess.waitFor(TIMEOUT_MS, TimeUnit.MILLISECONDS);
//            if (!runFinished) {
//                runProcess.destroy();
//                System.err.println("程序运行超时");
//                return "运行超时（超过" + TIMEOUT_MS + "ms）";
//            }
//
//            // 读取输出
//            String fullOutput = readStream(runProcess.getInputStream()).trim();
//            System.out.println("C++运行结果：输入=" + input + "，输出=" + fullOutput);
//
//            // 检查退出码
//            int runExitCode = runProcess.exitValue();
//            System.out.println("程序退出码: " + runExitCode);
//            if (runExitCode != 0) {
//                return "程序运行错误，退出码: " + runExitCode + "，输出: " + fullOutput;
//            }
//
//            if (fullOutput.isEmpty()) {
//                System.out.println("程序没有产生输出");
//                return "运行成功，输出：";
//            }
//            return "运行成功，输出：" + fullOutput;
//
//        } catch (Exception e) {
//            System.err.println("执行C++代码时发生异常：" + e.getMessage());
//            e.printStackTrace();
//            throw e;
//        } finally {
//            System.out.println("=== C++代码执行结束 ===");
//            // 清理进程和文件
//            if (compileProcess != null) compileProcess.destroy();
//            if (runProcess != null) runProcess.destroy();
//
//            // 延迟删除文件
//            try {
//                Thread.sleep(100);
//                new java.io.File(cppPath).delete();
//                java.io.File exeFile = new java.io.File(executablePath);
//                if (exeFile.exists()) {
//                    exeFile.delete();
//                }
//            } catch (Exception e) {
//                System.err.println("删除C++文件失败：" + e.getMessage());
//            }
//        }
//    }
//
//    // 在系统中查找合适的C++编译器
//    private String findCppCompiler() {
//        System.out.println("开始查找C++编译器...");
//        // 检查常用的C++编译器
//        String[] possibleCompilers = IS_WINDOWS
//                ? new String[]{"g++", "gcc", "cl", "mingw32-g++", "mingw64-g++"} // 增加更多可能的Windows编译器
//                : new String[]{"g++", "clang++"};  // Linux/Mac上可能的编译器
//
//        for (String compiler : possibleCompilers) {
//            System.out.println("检查编译器: " + compiler);
//            if (isCommandAvailable(compiler)) {
//                System.out.println("找到可用的编译器: " + compiler);
//                return compiler;
//            }
//        }
//        System.err.println("未找到可用的C++编译器");
//        return null; // 未找到编译器
//    }
//
//    // 检查命令是否可用
//    private boolean isCommandAvailable(String command) {
//        try {
//            ProcessBuilder pb = new ProcessBuilder(
//                    IS_WINDOWS ? new String[]{"cmd.exe", "/c", command, "--version"}
//                            : new String[]{"sh", "-c", command + " --version"}
//            );
//            pb.redirectErrorStream(true);
//            Process p = pb.start();
//            boolean finished = p.waitFor(2000, TimeUnit.MILLISECONDS); // 最多等待2秒
//            if (finished && p.exitValue() == 0) {
//                String output = readStream(p.getInputStream());
//                System.out.println("编译器版本信息: " + output.substring(0, Math.min(100, output.length())) + "...");
//                return true;
//            }
//            return false;
//        } catch (Exception e) {
//            System.err.println("检查命令可用性时出错: " + e.getMessage());
//            return false;
//        }
//    }
//
//// ... existing code ...
//
//    // Java代码运行逻辑保持不变...
//    private String runJava(String javaPath, String input, String className) throws Exception {
//        // 保留原有代码
//        Process compileProcess = null;
//        Process runProcess = null;
//
//        try {
//            // 编译Java代码
//            System.out.println("编译Java代码: " + javaPath);
//            ProcessBuilder compilePB = new ProcessBuilder("javac", javaPath);
//            compilePB.redirectErrorStream(true);
//            compilePB.directory(new java.io.File(TEMP_PATH));
//
//            compileProcess = compilePB.start();
//            boolean compileFinished = compileProcess.waitFor(TIMEOUT_MS, TimeUnit.MILLISECONDS);
//
//            if (!compileFinished) {
//                compileProcess.destroy();
//                return "编译超时（超过" + TIMEOUT_MS + "ms）";
//            }
//
//            int exitCode = compileProcess.exitValue();
//            if (exitCode != 0) {
//                String errorOutput = readStream(compileProcess.getInputStream());
//                return "编译失败：" + errorOutput;
//            }
//
//            // 运行Java程序
//            System.out.println("执行Java程序: " + className);
//            ProcessBuilder runPB = new ProcessBuilder("java", "-cp", TEMP_PATH, className);
//            runPB.redirectErrorStream(true);
//            runPB.directory(new java.io.File(TEMP_PATH));
//
//            runProcess = runPB.start();
//
//            // 写入输入
//            try (java.io.OutputStream os = runProcess.getOutputStream()) {
//                os.write(input.getBytes(StandardCharsets.UTF_8));
//                os.flush();
//                os.close(); // 立即关闭输入流
//            }
//
//            // 超时检测
//            boolean runFinished = runProcess.waitFor(TIMEOUT_MS, TimeUnit.MILLISECONDS);
//            if (!runFinished) {
//                runProcess.destroy();
//                return "运行超时（超过" + TIMEOUT_MS + "ms）";
//            }
//
//            // 读取输出
//            String fullOutput = readStream(runProcess.getInputStream()).trim();
//            System.out.println("Java运行结果：输入=" + input + "，输出=" + fullOutput);
//
//            if (fullOutput.isEmpty()) {
//                return "运行成功，输出：";
//            }
//            return "运行成功，输出：" + fullOutput;
//
//        } finally {
//            // 清理进程和文件
//            if (compileProcess != null) compileProcess.destroy();
//            if (runProcess != null) runProcess.destroy();
//
//            // 延迟删除文件
//            try {
//                Thread.sleep(100);
//                new java.io.File(javaPath).delete();
//                new java.io.File(TEMP_PATH + className + ".class").delete();
//            } catch (Exception e) {
//                System.err.println("删除Java文件失败：" + e.getMessage());
//            }
//        }
//    }
//
//    // Python代码运行逻辑保持不变...
//    private String runPython(String pyPath, String input) throws Exception {
//        Process runProcess = null;
//        try {
//            // 根据操作系统选择Python命令
//            String pythonCmd = IS_WINDOWS ? "python" : "python3";
//            System.out.println("执行Python命令: " + pythonCmd + " " + pyPath);
//
//            ProcessBuilder pb = new ProcessBuilder(pythonCmd, pyPath);
//            pb.redirectErrorStream(true); // 合并标准输出和错误输出
//
//            // 设置工作目录为临时目录
//            pb.directory(new java.io.File(TEMP_PATH));
//
//            runProcess = pb.start();
//
//            // 写入输入
//            try (java.io.OutputStream os = runProcess.getOutputStream()) {
//                os.write(input.getBytes(StandardCharsets.UTF_8));
//                os.flush();
//                // 立即关闭输入流，避免Python等待更多输入
//                os.close();
//            }
//
//            // 超时检测
//            boolean runFinished = runProcess.waitFor(TIMEOUT_MS, TimeUnit.MILLISECONDS);
//            if (!runFinished) {
//                runProcess.destroy();
//                return "运行超时（超过" + TIMEOUT_MS + "ms）";
//            }
//
//            // 读取所有输出
//            String fullOutput = readStream(runProcess.getInputStream()).trim();
//            System.out.println("Python运行结果：输入=" + input + "，输出=" + fullOutput);
//
//            // 无输出时返回空字符串而不是0
//            if (fullOutput.isEmpty()) {
//                return "运行成功，输出：";
//            }
//            return "运行成功，输出：" + fullOutput;
//
//        } finally {
//            if (runProcess != null) runProcess.destroy();
//            // 延迟删除文件，确保读取完成
//            try {
//                Thread.sleep(100);
//                new java.io.File(pyPath).delete();
//            } catch (Exception e) {
//                System.err.println("删除Python文件失败：" + e.getMessage());
//            }
//        }
//    }
//
//    // 读取流的通用方法
//    private String readStream(InputStream stream) throws Exception {
//        try (BufferedReader reader = new BufferedReader(
//                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
//            StringBuilder sb = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line).append("\n");
//            }
//            return sb.toString().trim();
//        }
//    }
//}