package oj.controller;

import lombok.extern.slf4j.Slf4j;
import oj.constant.pojo.Result;
import oj.constant.dto.SubmitTestQuestionDTO;
import oj.constant.vo.SubmitTestQuestionVO;
import oj.constant.vo.TestPointVO;
import oj.constant.vo.TestQuestionVO;
import oj.service.TestQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/user/testQuestion")
public class UserTestQuestionController {
    @Autowired
    private TestQuestionService testQuestionService;

    @GetMapping("/getTestQuestionByPage")
    public Result getTestQuestionByPage(@RequestParam Integer pageNum, @RequestParam Integer size,@RequestParam(required = true) String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            keyword = "";
        }
        log.info("分页查询测试题目: {}, {}, {}", pageNum, size,keyword);
        List<TestQuestionVO> testQuestionVOList = testQuestionService.SelectTestQuestionByPage(pageNum, size, keyword);
        return Result.success(testQuestionVOList);
    }

//    查询指定的题目，根据id
    @GetMapping("/getTestQuestionById")
    public Result getTestQuestionById(@RequestParam String id) {
        log.info("根据id查询测试题目: {}", id);
        TestQuestionVO testQuestionVO = testQuestionService.SelectTestQuestionById(id);
        return Result.success(testQuestionVO);
    }

    @PostMapping("/submitTestQuestion")
    public Result submitTestQuestion(@RequestBody SubmitTestQuestionDTO submitTestQuestionDTO) {
        log.info("提交测试题目: {}", submitTestQuestionDTO);
        SubmitTestQuestionVO submitTestQuestionVO = testQuestionService.submitTestQuestion(submitTestQuestionDTO);
        return Result.success(submitTestQuestionVO);
    }

//    根据题目名字模糊查询测试题目
    @GetMapping("/getTestQuestionByName")
    public Result getTestQuestionByName(@RequestParam String name) {
        log.info("根据名称模糊查询测试题目: {}", name);
        List<TestQuestionVO> testQuestionVOList = testQuestionService.SelectTestQuestionByName(name);
        return Result.success(testQuestionVOList);
    }

    @GetMapping("/getTestPointsListByQuestionId")
    public Result getTestPointsByQuestionId(@RequestParam String id) {
        log.info("根据测试题目id查询测试点: {}", id);
        List<TestPointVO> testPointVOList = testQuestionService.selectTestPointsByQuestionId(id);
        return Result.success(testPointVOList);
    }




}
































