package oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import oj.constant.dto.AddTestQuestionDTO;
import oj.constant.dto.SubmitTestQuestionDTO;
import oj.constant.entity.Questions;
import oj.constant.vo.SubmitTestQuestionVO;
import oj.constant.vo.TestPointVO;
import oj.constant.vo.TestQuestionVO;

import java.util.List;

public interface TestQuestionService extends IService<Questions> {
     String addTestQuestion(AddTestQuestionDTO addTestQuestionDTO);

     void deleteTestQuestionById(String id);

     String UpdateTestQuestion(AddTestQuestionDTO addTestQuestionDTO);

     List<TestQuestionVO> SelectTestQuestionByPage(Integer pageNum, Integer size, String keyword);

     TestQuestionVO SelectTestQuestionById(String id);

     SubmitTestQuestionVO submitTestQuestion(SubmitTestQuestionDTO submitTestQuestionDTO);

     List<TestPointVO> selectTestPointsByQuestionId(String id);

     List<TestQuestionVO> SelectTestQuestionByName(String name);

     Integer selectTestQuestionCount(String keyword);
}