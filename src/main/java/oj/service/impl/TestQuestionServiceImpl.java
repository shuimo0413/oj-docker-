package oj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import oj.constant.IPConstants;
import oj.constant.LanguageConstants;
import oj.constant.TestPointConstants;
import oj.pojo.entity.Judge0Token;
import oj.pojo.entity.Questions;
import oj.pojo.entity.UserSubmissionCode;
import oj.mapper.CodeMapper;
import oj.mapper.TestQuestionMapper;
import oj.pojo.entity.QuestionResult;
import oj.pojo.dto.AddTestQuestionDTO;
import oj.pojo.dto.SubmitTestQuestionDTO;
import oj.pojo.dto.TestPointDTO;
import oj.pojo.rpc.SubmissionRecordRpcRequest;
import oj.pojo.rpc.SubmissionRecordRpcResponse;
import oj.pojo.vo.SubmitTestQuestionVO;
import oj.pojo.vo.TestPointVO;
import oj.pojo.vo.TestQuestionVO;
import oj.service.TestQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TestQuestionServiceImpl extends ServiceImpl<TestQuestionMapper, Questions> implements TestQuestionService {
    @Autowired
    private TestQuestionMapper testQuestionMapper;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CodeMapper codeMapper;

    private static final String JUDGE0_SUBMISSION_URL = IPConstants.JUDGE0IP
            + "/submissions?base64_encoded=false&wait=false";
    private static final String JUDGE0_GET_SUBMISSION_URL = IPConstants.JUDGE0IP;
    @Override
    public String addTestQuestion(AddTestQuestionDTO addTestQuestionDTO) {
        addTestQuestionDTO.setCreateTime(LocalDateTime.now());
        addTestQuestionDTO.setUpdateTime(LocalDateTime.now());

        testQuestionMapper.addTestQuestion(addTestQuestionDTO);
//
        List<TestPointDTO> testPoints = addTestQuestionDTO.getTestPointList();
//        每一个都赋值测试题目id
        for (TestPointDTO testPointDTO : testPoints) {
            testPointDTO.setQuestionId(addTestQuestionDTO.getId());
            testPointDTO.setCreateTime(LocalDateTime.now());
            testPointDTO.setUpdateTime(LocalDateTime.now());
        }
        log.info("添加测试点: {}", testPoints);


        testQuestionMapper.addTestPoints(testPoints);

        return "添加测试题目成功";
    }

    @Override
    public void deleteTestQuestionById(String id) {
        log.info("根据id删除测试题目: {}", id);
        testQuestionMapper.deleteTestQuestionByIdFromQuestions(id);
        testQuestionMapper.deleteTestQuestionByIdFromTestPoints(id);
//        return "删除测试题目成功";
    }

    @Override
    public String UpdateTestQuestion(AddTestQuestionDTO addTestQuestionDTO) {
//        先删除之前的题目
        String id = addTestQuestionDTO.getId();
        deleteTestQuestionById(id);
//        更新题目
        addTestQuestion(addTestQuestionDTO);
        return "更新测试题目成功";
    }

    @Override
    public List<TestQuestionVO> SelectTestQuestionByPage(Integer pageNum, Integer size, String keyword) {
        String queryKeyword = "";


        if (keyword != null && !keyword.isEmpty()) {
            //        String name1 = "";
            for (int i = 0; i < keyword.length(); i++) {
                queryKeyword += "%" + keyword.charAt(i);
            }
            queryKeyword += "%";
        }else {
            queryKeyword = "%";
        }

        // 分页起始值计算（补充：校验pageNum避免负数）
        Integer start = (pageNum - 1) * size;
        if (start < 0) {
            start = 0; // 防止pageNum=0/负数导致查询错误
        }

        // 调用Mapper，传入正确的模糊查询关键词
        List<TestQuestionVO> testQuestionVOS = testQuestionMapper.selectTestQuestionByPage(start, size, queryKeyword);
//        查询题目的总数

        return testQuestionVOS;
    }

    @Override
    public TestQuestionVO SelectTestQuestionById(String id) {
        TestQuestionVO testQuestionVO = testQuestionMapper.selectTestQuestionById(id);
        return testQuestionVO;
    }








//    判题模块
    @Override
    public SubmitTestQuestionVO submitTestQuestion(SubmitTestQuestionDTO submitTestQuestionDTO) {
//        查询题目,保证题目一定能查询到
        Questions question = testQuestionMapper.selectById(submitTestQuestionDTO.getId());
        Float limitedTime;

        if (question == null) {
            log.error("题目ID {} 不存在，无法提交", submitTestQuestionDTO.getId());
            throw new IllegalArgumentException("题目不存在");
        }

        if(question.getLimitedTime() != null){
            limitedTime = (question.getLimitedTime() / 1000.0F);
        }else{
            limitedTime = 1.0F;

        }

        UserSubmissionCode userSubmissionCode = new UserSubmissionCode();
        userSubmissionCode.setCode(submitTestQuestionDTO.getAnswer());
        userSubmissionCode.setUserId(Integer.valueOf(submitTestQuestionDTO.getUserId()));
        userSubmissionCode.setCreateTime(LocalDateTime.now());
        userSubmissionCode.setUpdateTime(LocalDateTime.now());
        userSubmissionCode.setQuestionId(Integer.valueOf(question.getId()));

        codeMapper.insert(userSubmissionCode);
//        主键返回
        Integer codeId = userSubmissionCode.getId();


        //                获取答案使用的语言
        String languageStr = submitTestQuestionDTO.getLanguage();
        Integer languageId = null;
        if(languageStr != null && !languageStr.isEmpty()) {
            String upperLanguage = languageStr.toUpperCase();
            languageId = LanguageConstants.LANGUAGE_NAME_TO_ID.get(upperLanguage);
        }

        SubmitTestQuestionVO resultVO = new SubmitTestQuestionVO();
        resultVO.setId(submitTestQuestionDTO.getId());
        List<TestPointVO> testPointList = testQuestionMapper.selectTestPointsByQuestionId(submitTestQuestionDTO.getId());
        log.info("根据id查询测试点: {}", testPointList);

//        测试点结果的list
        List<QuestionResult> questionResultList = new ArrayList<>();



        for (TestPointVO testPointVO : testPointList){
            try {
                SubmissionRecordRpcRequest submissionRecordRpcRequest = new SubmissionRecordRpcRequest();
                submissionRecordRpcRequest.setStdin(testPointVO.getInput());
                submissionRecordRpcRequest.setCpuTimeLimit(limitedTime);
                submissionRecordRpcRequest.setSourceCode(submitTestQuestionDTO.getAnswer());
                submissionRecordRpcRequest.setMemoryLimit(128000L);

                submissionRecordRpcRequest.setLanguageId(languageId);

                Judge0Token token = restTemplate.postForObject(
                        JUDGE0_SUBMISSION_URL,
                        submissionRecordRpcRequest,
                        Judge0Token.class);


                log.info("提交记录: {}", token);
                QuestionResult queryResult = queryJudgeResultAsync(token.getToken(),
                        question.getId(),
                        testPointVO.getQuestionId(),
                        languageStr,codeId,
                        Integer.valueOf(submitTestQuestionDTO.getUserId()),
                        testPointVO.getOutput()
                );
                questionResultList.add(queryResult);



            }catch (Exception e){
                log.error("请求判题服务失败: {}", e.getMessage());
            }

        }
        resultVO.setQuestionResultList(questionResultList);
        return resultVO;
    }

    @Async("judgeExecutor")
    public QuestionResult queryJudgeResultAsync(String token, String questionId, String testPointId,String languageStr,Integer codeId,Integer userId,String output) throws InterruptedException {
        int maxRetry = 10; // 最大重试次数
        int retryInterval = 1000; // 轮询间隔
        int retryCount = 0;
        boolean isFinished = false;

        Thread.sleep(500);

        while (retryCount < maxRetry && !isFinished) {
            try {

//            轮询检测
                log.info("轮询测试点{}，token:{}，第{}次重试", testPointId, token, retryCount + 1);

                SubmissionRecordRpcResponse response = restTemplate.getForObject(
                        JUDGE0_GET_SUBMISSION_URL + "/submissions/" + token + "?base64_encoded=false",
                        SubmissionRecordRpcResponse.class);
                log.info(JUDGE0_GET_SUBMISSION_URL + "/submissions/" + token + "?base64_encoded=false");


                if (response == null) {
                    log.warn("测试点{}轮询结果为空，token:{}，第{}次重试", testPointId, token, retryCount + 1);
                    retryCount++;
                    Thread.sleep(retryInterval);
                    continue;
                }


                if (response.getTime() != null) {
//                    log.info("测试完成{}:", response);
//                    log.info("测试完成------------{}", response.getStatus());
                    testQuestionMapper.addTestPointResult(
                            testPointId,
                            response,
                            languageStr,
                            Integer.valueOf(questionId),
                            codeId,
                            userId
                    );

//                    判断是否通过
                    if (response.getStatus().getId() == 3) {
//                        去掉尾部空格,\n,\r再说
                        if (StringUtils.trimTrailingWhitespace(response.getStdout()).equals(StringUtils.trimTrailingWhitespace(output))) {
                            return new QuestionResult(TestPointConstants.AC);
                        } else {
                            return new QuestionResult(TestPointConstants.WA);
                        }
                    } else {
                        if (response.getStatus().getId() == 5) {
                            return new QuestionResult(TestPointConstants.TLE);
                        } else {
                            return new QuestionResult(TestPointConstants.RE);
                        }

                    }
//                    break;
                } else {
                    retryCount++;
                    Thread.sleep(retryInterval);
                }
            } catch (Exception e) {
                log.error("轮询测试点{}失败: {}", e.getMessage());
                break;

            }

        }

        return new QuestionResult(TestPointConstants.RE);

    }



    @Override
    public List<TestPointVO> selectTestPointsByQuestionId(String id) {
        return testQuestionMapper.selectTestPointsByQuestionId(id);
    }

//    根据题目名字模糊查询测试题目
    @Override
    public List<TestQuestionVO> SelectTestQuestionByName(String name) {
//        给每一个字符前面都加%
        String name1 = "";
        for (int i = 0; i < name.length(); i++) {
            name1 += "%" + name.charAt(i);
        }
        name1 += "%";
        log.info("根据名称模糊查询测试题目: {}", name1);
        List<TestQuestionVO> testQuestionVOList = testQuestionMapper.selectTestQuestionsByName(name1);
        return testQuestionVOList;
    }

     @Override
    public Integer selectTestQuestionCount(String keyword) {
        return testQuestionMapper.selectTestQuestionCount(keyword);
    }

}
