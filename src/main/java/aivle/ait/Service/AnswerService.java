package aivle.ait.Service;

import aivle.ait.Dto.AnswerDTO;
import aivle.ait.Dto.QuestionDTO;
import aivle.ait.Entity.Answer;
import aivle.ait.Entity.Question;
import aivle.ait.Repository.AnswerRepository;
import aivle.ait.Repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public AnswerDTO create(Long boardId, AnswerDTO answerDto){
        Optional<Question> questions = questionRepository.findById(boardId);
        if (questions.isEmpty()){
            return null;
        }
        Question question = questions.get();

        Answer answer = new Answer();
        answer.setQuestion(question);

        Answer createdAnswer = answerRepository.save(answer);
        AnswerDTO createdAnswerDTO = new AnswerDTO(createdAnswer);

        return createdAnswerDTO;
    }

    public String readOne(Long question_id){
        Question question = questionRepository.getReferenceById(question_id);
        if (question == null){
            return null;
        }
        QuestionDTO questionDto = new QuestionDTO(question);

        return questionDto.getAnswer();
    }

    @Transactional
    public AnswerDTO update(Long id, String content){
        Optional<Answer> answers = answerRepository.findById(id);
        if (answers.isEmpty()){
            return null;
        }

        Answer answer = answers.get();
        answer.setContent(content);

        AnswerDTO answerDto = new AnswerDTO(answer);
        return answerDto;
    }

    @Transactional
    public AnswerDTO delete(Long id){
        Optional<Answer> comments = answerRepository.findById(id);
        if (comments.isEmpty()){
            return null;
        }

        Answer answer = comments.get();
        answerRepository.delete(answer);

        AnswerDTO answerDto = new AnswerDTO(answer);
        return answerDto;
    }
}
