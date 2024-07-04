package aivle.ait.Service;

import aivle.ait.Dto.QuestionDTO;
import aivle.ait.Entity.Company;
import aivle.ait.Entity.Question;
import aivle.ait.Repository.CompanyRepository;
import aivle.ait.Repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {
    private final CompanyRepository companyRepository;
    private final QuestionRepository questionRepository;
    
    @Transactional
    public QuestionDTO create(Long companyId, QuestionDTO questionDto){
        Optional<Company> company = companyRepository.findById(companyId);

        if (company.isEmpty()) {
            return null;
        }

        Question question = new Question();
        question.setDtoToObject(questionDto);
        question.setCompany(company.get());

        Question createdQuestion = questionRepository.save(question);
        QuestionDTO createdQuestionDto = new QuestionDTO(createdQuestion);

        return createdQuestionDto;
    }

    public QuestionDTO readOne(Long question_id){
        Optional<Question> question =  questionRepository.findById(question_id);

        if (question.isEmpty()) {
            return null;
        }

        QuestionDTO questionDto = new QuestionDTO(question.get());
        return questionDto;
    }

    public List<QuestionDTO> readAll(){
        List<Question> questions =  questionRepository.findAll();
        List<QuestionDTO> questionDTOs = QuestionDTO.convertToDto(questions);
        return questionDTOs;
    }

    public Page<QuestionDTO> readAllPageable(Long companyId, Pageable pageable){
        Page<Question> questions = questionRepository.findByCompanyId(companyId, pageable);
        Page<QuestionDTO> questionDTOS = QuestionDTO.toDtoPage(questions);
        return questionDTOS;
    }

    public List<QuestionDTO> readMyQuestion(Long company_id){
        Optional<Company> company = companyRepository.findById(company_id);

        if (company.isEmpty()) {
            return null;
        }

        List<QuestionDTO> questionDTOs = QuestionDTO.convertToDto(company.get().getQuestions());
        return questionDTOs;
    }

    public boolean checkMyQuestion(Long id, String writer){
        Optional<Question> questions = questionRepository.findById(id);
        if (questions.isEmpty()){
            return false;
        }

        QuestionDTO questionDto = new QuestionDTO(questions.get());
        boolean result = questionDto.getCompany().equals(writer);
        return result;
    }

    @Transactional
    public QuestionDTO update(Long id, QuestionDTO questionDTO){
        Optional<Question> questions = questionRepository.findById(id);
        if (questions.isEmpty()){
            return null;
        }

        Question question = questions.get();
        question.setTitle(questionDTO.getTitle());
        question.setContent(questionDTO.getContent()); // update

        QuestionDTO questionDto = new QuestionDTO(question);
        return questionDto;
    }

    @Transactional
    public QuestionDTO delete(Long id){
        Optional<Question> questions = questionRepository.findById(id);
        if (questions.isEmpty()){
            return null;
        }

        Question question = questions.get();
        questionRepository.delete(question);

        QuestionDTO questionDto = new QuestionDTO(question);
        return questionDto;
    }

    public List<QuestionDTO> getQustionKeyword(String keyword){
        List<Question> questions = questionRepository.findCustomByTitleContaining(keyword);
        List<QuestionDTO> questionDTOs = QuestionDTO.convertToDto(questions);
        return questionDTOs;
    }
}
