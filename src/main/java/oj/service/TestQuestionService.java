package oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import oj.pojo.dto.AddTestQuestionDTO;
import oj.pojo.dto.SubmitTestQuestionDTO;
import oj.pojo.entity.Questions;
import oj.pojo.vo.SubmitTestQuestionVO;
import oj.pojo.vo.TestPointVO;
import oj.pojo.vo.TestQuestionVO;

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