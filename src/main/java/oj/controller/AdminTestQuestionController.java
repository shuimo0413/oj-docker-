package oj.controller;

import lombok.extern.slf4j.Slf4j;
import oj.constant.pojo.Result;
import oj.constant.dto.AddTestQuestionDTO;
import oj.constant.vo.TestPointVO;
import oj.constant.vo.TestQuestionVO;
import oj.service.TestQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/testQuestion")
public class AdminTestQuestionController {
    @Autowired
    private TestQuestionService testQuestionService;


//    题目分页查询
    @GetMapping("/getTestQuestionByPage")
    public Result getTestQuestionByPage(@RequestParam Integer pageNum, @RequestParam Integer size,@RequestParam(required = true) String keyword) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        log.info("分页查询测试题目: {}, {}, {}", pageNum, size, keyword);
        List<TestQuestionVO> testQuestionVOList = testQuestionService.SelectTestQuestionByPage(pageNum, size,keyword);
        return Result.success(testQuestionVOList);
    }

    @GetMapping("/getTestQuestionCount")
    public Result getTestQuestionCount(@RequestParam String keyword) {
        log.info("查询测试题目总数");
        Integer total = testQuestionService.selectTestQuestionCount(keyword);
        return Result.success(total);
    }

    @GetMapping("/getTestQuestionById")
    public Result getTestQuestionById(@RequestParam String id) {
        log.info("根据id查询测试题目: {}", id);
        TestQuestionVO testQuestionVO = testQuestionService.SelectTestQuestionById(id);
        return Result.success(testQuestionVO);
    }

//    根据id删除测试题目
    @DeleteMapping("/deleteTestQuestionById")
    public Result deleteTestQuestionById(@RequestParam String id) {
        log.info("根据id删除测试题目: {}", id);
        testQuestionService.deleteTestQuestionById(id);
        return Result.success();
    }
//    添加测试题目
    @PostMapping("/addTestQuestion")
    public Result addTestQuestion(@RequestBody AddTestQuestionDTO addTestQuestionDTO) {
        log.info("添加测试题目: {}", addTestQuestionDTO);
        String message = testQuestionService.addTestQuestion(addTestQuestionDTO);
        return Result.success(message);
    }


//    更新测试题目
    @PostMapping("/updateTestQuestion")
    public Result updateTestQuestion(@RequestBody AddTestQuestionDTO addTestQuestionDTO) {
        log.info("更新测试题目: {}", addTestQuestionDTO);
        String message = testQuestionService.UpdateTestQuestion(addTestQuestionDTO);
        return Result.success(message);
    }

//    查询测试点
    @GetMapping("/getTestPointsListByQuestionId")
    public Result getTestPointsByQuestionId(@RequestParam String id) {
        log.info("根据测试题目id查询测试点: {}", id);
        List<TestPointVO> testPointVOList = testQuestionService.selectTestPointsByQuestionId(id);
        return Result.success(testPointVOList);
    }

//    根据名称模糊查询测试题目
    @GetMapping("/getTestQuestionByName")
    public Result getTestQuestionByName(@RequestParam String name) {
        log.info("根据名称模糊查询测试题目: {}", name);
        List<TestQuestionVO> testQuestionVOList = testQuestionService.SelectTestQuestionByName(name);
        return Result.success(testQuestionVOList);
    }

}
