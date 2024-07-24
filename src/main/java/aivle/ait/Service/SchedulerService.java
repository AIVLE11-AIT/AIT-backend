package aivle.ait.Service;

import aivle.ait.Entity.InterviewGroup;
import aivle.ait.Entity.Interviewer;
import aivle.ait.Entity.Result;
import aivle.ait.Repository.InterviewGroupRepository;
import aivle.ait.Repository.InterviewerRepository;
import aivle.ait.Repository.ResultRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchedulerService {
    private final InterviewerRepository interviewerRepository;
    private final InterviewGroupRepository interviewGroupRepository;
    private final ResultService resultService;
    private final ResultRepository resultRepository;

    // 스케줄러를 1분 간겨으로 돌려서 면접 날짜가 끝난 면접 그룹을 찾음
    // 그룹 안의 인터뷰어들의 세부파트 결과를 바탕으로 analyze를 진행.
    @Scheduled(fixedDelay = 60000) // 1분마다 스케줄러 실행
    @Transactional
    public void scheduleAnalyze() {
        System.out.println("스케줄러 실행");
        LocalDateTime now = LocalDateTime.now();
        List<InterviewGroup> groups = interviewGroupRepository.findAll();

        for (InterviewGroup group: groups) {
            if (group.getEnd_date().isAfter(now)) continue; // 면접이 아직 종료되지 않았으면 넘어감
            LocalDateTime end_date = group.getEnd_date();
            //Duration duration = Duration.between(end_date, now);
            //if (duration.toHours() < 1) continue; // 면접 종료가 1시간 이상 지나야지 분석 가능

            List<Interviewer> interviewers = interviewerRepository.findByInterviewgroupId(group.getId());
            if (interviewers.isEmpty()) {
                System.out.println("인터뷰 그룹이 없음. " + group.getId());
                continue;
            }
            for(Interviewer interviewer: interviewers) {
                Optional<Result> resultOptional = resultRepository.findByInterviewerId(interviewer.getId());
                if (!resultOptional.isEmpty()) continue; // 최종 레포트가 이미 있으면 분석하지 않음
                System.out.println(interviewer.getId());
                Optional<Interviewer> interviewerOptional = interviewerRepository.findById(interviewer.getId());

                // file이 1개라도 있으면 분석
                int fileCnt = interviewerOptional.get().getFiles().size();
                if (fileCnt <= 0) continue;
                resultService.analyze(group.getId(), interviewer.getId());
            }
        }
    }
}
