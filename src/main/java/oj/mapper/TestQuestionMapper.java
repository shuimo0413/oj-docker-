package oj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import oj.pojo.dto.AddTestQuestionDTO;
import oj.pojo.dto.TestPointDTO;
import oj.pojo.entity.Questions;
import oj.pojo.rpc.SubmissionRecordRpcResponse;
import oj.pojo.vo.TestPointVO;
import oj.pojo.vo.TestQuestionVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TestQuestionMapper extends BaseMapper<Questions> {
    void addTestPointResult(
            @Param("testPointId") String testPointId,
            @Param("response") SubmissionRecordRpcResponse response,
            @Param("languageStr") String languageStr,
            @Param("questionId") Integer questionId,
            @Param("codeId") Integer codeId,
            @Param("userId") Integer userId);



    void addTestQuestion(AddTestQuestionDTO addTestQuestionDTO);

    void addTestPoints(@Param("testPoints") List<TestPointDTO> testPoints);

    void deleteTestQuestionByIdFromQuestions(String id);

    void deleteTestQuestionByIdFromTestPoints(String id);

    List<TestQuestionVO> selectTestQuestionByPage(@Param("start") Integer start, @Param("size") Integer size, @Param("keyword") String keyword);

    TestQuestionVO selectTestQuestionById(@Param("id") String id);

    List<TestPointVO> selectTestPointsByQuestionId(String id);

    List<TestQuestionVO> selectTestQuestionsByName(@Param("name") String name);

    Integer selectTestQuestionCount(@Param("keyword") String keyword);
}