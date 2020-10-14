package report;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import model.Answer;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

public class Peformance {
    
    public static void render(List<Answer> answers, long remainingMiliseconds) throws JRException {
        answers = answers.stream().filter(a -> a.isAnswered()).collect(Collectors.toList());
        
        HashMap params = new HashMap();
        
        params.put("QUESTION_NUMBER", answers.size());
        params.put("ACTUAL_DATE", (new Date()).getTime());
        params.put("CONCLUSION_TIME", answers.stream().mapToLong(a -> a.getAnswerMiliseconds()).sum());
        params.put("REMAINING_TIME", remainingMiliseconds);
        
        JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(answers);
        JasperPrint printer = JasperFillManager.fillReport("jasper/peformance.jasper", params, datasource);
        JasperViewer viewer = new JasperViewer(printer, false);
        viewer.setLocationRelativeTo(null);
        viewer.setVisible(true);
    }
}
