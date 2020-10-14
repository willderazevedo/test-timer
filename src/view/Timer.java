package view;

import filter.IntegerFilter;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.text.PlainDocument;
import model.Answer;
import report.Peformance;

public class Timer extends javax.swing.JFrame {

    private int questionNumber;
    private long remainingMiliseconds;
    private long endMiliseconds;
    private long minQuestionMiliseconds;
    private Thread remainingThread;
    private Thread questionThread;
    private Thread endThread;
    private final List<Answer> answers = new ArrayList<>();
    
    public Timer() {
        initComponents();
        setLocationRelativeTo(null);
        
        ((PlainDocument) txtQuestions.getDocument()).setDocumentFilter(new IntegerFilter());
        radioFirstOption.setActionCommand(radioFirstOption.getText());
        radioSecondOption.setActionCommand(radioSecondOption.getText());
        radioThirdOption.setActionCommand(radioThirdOption.getText());
        radioFourthOption.setActionCommand(radioFourthOption.getText());
        radioFifthOption.setActionCommand(radioFifthOption.getText());
    }
    
    private Thread remainingTimer() {
        return new Thread () {
            @Override
            public void run() {
                while (remainingMiliseconds >= 0 || !Thread.interrupted()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        break;
                    }

                    remainingMiliseconds -= 1000;    

                    if (remainingMiliseconds < 0) {
                        long remainingTime = remainingMiliseconds;
                        
                        toggleTimer(true);
                        JOptionPane.showMessageDialog(null, "Tempo esgotado", "Timer", JOptionPane.INFORMATION_MESSAGE);
                        
                        if (answers.size() > 1) {
                            try {
                                Peformance.render(answers, remainingTime);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Não foi possível gerar o relatório:\n"  + ex.getMessage(), "Timer", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        
                        answers.clear();

                        break;
                    }

                    lblRemainingTime.setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(remainingMiliseconds),
                        TimeUnit.MILLISECONDS.toMinutes(remainingMiliseconds) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(remainingMiliseconds) % TimeUnit.MINUTES.toSeconds(1))
                    );
                }
            }
        };
    }
    
    private Thread questionTimer(Answer answer) {
        return new Thread () {
            @Override
            public void run() {
                while (true){
                    if (answer.isAnswered() || Thread.interrupted()) {
                        lblQuestionTime.setText("00:00");
                        lblQuestionTime.setForeground(new Color(40, 40, 40));

                        break;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        lblQuestionTime.setText("00:00");
                        lblQuestionTime.setForeground(new Color(40, 40, 40));
                        
                        break;
                    }
                    
                    answer.setAnswerMiliseconds(answer.getAnswerMiliseconds() + 1000);
                    answer.setExtraMiliseconds(answer.getMaxMiliseconds() - answer.getAnswerMiliseconds());

                    if (answer.getExtraMiliseconds() < 0 && !answer.isPenalty()) {
                        answer.setPenalty(true);
                        lblQuestionTime.setForeground(new Color(240, 52, 52));
                    }

                    lblQuestionTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(answer.getAnswerMiliseconds()) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(answer.getAnswerMiliseconds()) % TimeUnit.MINUTES.toSeconds(1))
                    );
                }
            }
        };
    }

    private Thread endTimer() {
        return new Thread ()  {
            @Override
            public void run() {
                while (endMiliseconds >= 0 || !Thread.interrupted()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        break;
                    }

                    endMiliseconds -= 1000;    

                    if (endMiliseconds < 0) endMiliseconds = remainingMiliseconds;
                    
                    if (endMiliseconds > remainingMiliseconds) {
                        lblEndTime.setForeground(new Color(240, 52, 52));
                    } else if (endMiliseconds == remainingMiliseconds) {
                        lblEndTime.setForeground(new Color(40, 40, 40));
                    } else {
                        lblEndTime.setForeground(new Color(30, 130, 76));
                    }

                    lblEndTime.setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(endMiliseconds),
                        TimeUnit.MILLISECONDS.toMinutes(endMiliseconds) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(endMiliseconds) % TimeUnit.MINUTES.toSeconds(1))
                    );
                }
            }
        };
    }
    
    private long timestamp(String time, boolean secondMode) {
        int partIndex = 0;
        long timestamp = 0;
        
        for (String timePart : time.split(":")) {
            
            if (partIndex == 0) timestamp += Integer.parseInt(timePart) * (!secondMode ? 36e+5 : 6e+4);
            if (partIndex == 1) timestamp += Integer.parseInt(timePart) * (!secondMode ? 6e+4 : 1e+3);
                    
            partIndex++;
        }
        
        return timestamp;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton2 = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        questionGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        txtQuestions = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnBegin = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        lblRemainingTime = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblMaxTime = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblQuestionTime = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblPenaltyAndExtraTime = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblEndTime = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblLatestTime = new javax.swing.JLabel();
        btnNextQuestion = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        lblOption = new javax.swing.JLabel();
        radioFirstOption = new javax.swing.JRadioButton();
        radioSecondOption = new javax.swing.JRadioButton();
        radioThirdOption = new javax.swing.JRadioButton();
        radioFourthOption = new javax.swing.JRadioButton();
        radioFifthOption = new javax.swing.JRadioButton();
        txtHours = new javax.swing.JFormattedTextField();
        txtMinQuestionTime = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();

        jButton2.setText("jButton2");

        jLabel15.setText("jLabel15");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Timer");

        jLabel1.setText("Questões");

        jLabel2.setText("Tempo");

        btnBegin.setText("Iniciar");
        btnBegin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBeginActionPerformed(evt);
            }
        });

        btnStop.setText("Parar");
        btnStop.setEnabled(false);
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        lblRemainingTime.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblRemainingTime.setForeground(new java.awt.Color(40, 40, 40));
        lblRemainingTime.setText("00:00:00");

        jLabel4.setText("Tempo restante");

        jLabel5.setText("Questão atual");

        lblMaxTime.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblMaxTime.setForeground(new java.awt.Color(40, 40, 40));
        lblMaxTime.setText("00:00");

        jLabel7.setText("Tempo máximo");

        lblQuestionTime.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblQuestionTime.setForeground(new java.awt.Color(40, 40, 40));
        lblQuestionTime.setText("00:00");

        jLabel9.setText("Acréscimo/Penalidades");

        lblPenaltyAndExtraTime.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblPenaltyAndExtraTime.setForeground(new java.awt.Color(40, 40, 40));
        lblPenaltyAndExtraTime.setText("00:00");

        jLabel11.setText("Previsão de Término");

        lblEndTime.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblEndTime.setForeground(new java.awt.Color(40, 40, 40));
        lblEndTime.setText("00:00:00");

        jLabel13.setText("Último Tempo");

        lblLatestTime.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblLatestTime.setForeground(new java.awt.Color(40, 40, 40));
        lblLatestTime.setText("00:00");

        btnNextQuestion.setText("Próxima questão");
        btnNextQuestion.setEnabled(false);
        btnNextQuestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextQuestionActionPerformed(evt);
            }
        });

        lblOption.setText("Alternativas da Questão");

        questionGroup.add(radioFirstOption);
        radioFirstOption.setSelected(true);
        radioFirstOption.setText("A");
        radioFirstOption.setEnabled(false);

        questionGroup.add(radioSecondOption);
        radioSecondOption.setText("B");
        radioSecondOption.setEnabled(false);

        questionGroup.add(radioThirdOption);
        radioThirdOption.setText("C");
        radioThirdOption.setEnabled(false);

        questionGroup.add(radioFourthOption);
        radioFourthOption.setText("D");
        radioFourthOption.setEnabled(false);

        questionGroup.add(radioFifthOption);
        radioFifthOption.setText("E");
        radioFifthOption.setEnabled(false);

        try {
            txtHours.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        try {
            txtMinQuestionTime.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtMinQuestionTime.setText("1:00");

        jLabel3.setText("Resposta");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnBegin, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnStop, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(txtQuestions, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtHours, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(txtMinQuestionTime, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnNextQuestion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(radioFirstOption)
                                        .addGap(10, 10, 10)
                                        .addComponent(radioSecondOption)
                                        .addGap(10, 10, 10)
                                        .addComponent(radioThirdOption)
                                        .addGap(10, 10, 10)
                                        .addComponent(radioFourthOption)
                                        .addGap(10, 10, 10)
                                        .addComponent(radioFifthOption))
                                    .addComponent(lblOption))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(lblQuestionTime)
                            .addComponent(jLabel13)
                            .addComponent(lblLatestTime))
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMaxTime)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9)
                            .addComponent(lblPenaltyAndExtraTime)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblRemainingTime)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(lblEndTime))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtQuestions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMinQuestionTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStop)
                    .addComponent(btnBegin))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel11))
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRemainingTime)
                    .addComponent(lblEndTime))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMaxTime))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblQuestionTime)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLatestTime)
                    .addComponent(lblPenaltyAndExtraTime))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblOption)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioFirstOption)
                    .addComponent(radioSecondOption)
                    .addComponent(radioThirdOption)
                    .addComponent(radioFourthOption)
                    .addComponent(radioFifthOption))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnNextQuestion)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void toggleTimer(Boolean stop) {
        txtQuestions.setEnabled(stop);
        txtHours.setEnabled(stop);
        txtMinQuestionTime.setEnabled(stop);
        btnBegin.setEnabled(stop);
        btnStop.setEnabled(!stop);
        btnNextQuestion.setEnabled(!stop);
        radioFirstOption.setEnabled(!stop);
        radioSecondOption.setEnabled(!stop);
        radioThirdOption.setEnabled(!stop);
        radioFourthOption.setEnabled(!stop);
        radioFifthOption.setEnabled(!stop);
        lblEndTime.setText("00:00:00");
        lblLatestTime.setText("00:00");
        lblMaxTime.setText("00:00");
        lblPenaltyAndExtraTime.setText("00:00");
        lblPenaltyAndExtraTime.setForeground(new Color(40, 40, 40));
        lblQuestionTime.setText("00:00");
        lblQuestionTime.setForeground(new Color(40, 40, 40));
        lblRemainingTime.setText("00:00:00");
        lblOption.setText("Alternativas da Questão");
        remainingMiliseconds = 0;
        questionNumber = 0;
        endMiliseconds = 0;
        
        if (stop) {
            remainingThread.interrupt();
            questionThread.interrupt();
            endThread.interrupt();
        }
    }
    
    private void btnBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBeginActionPerformed
        String questions = txtQuestions.getText();
        String testTime  = txtHours.getText().trim();
        String minTime   = txtMinQuestionTime.getText().trim();

        if (questions.isEmpty() || Integer.parseInt(questions) == 0 || testTime.equals(":") || minTime.equals(":")) {
            JOptionPane.showMessageDialog(null, "Preencha o número de questões, tempo de prova e o tempo mínimo para resposta.", "Timer", JOptionPane.INFORMATION_MESSAGE);
            
            return;
        }
        
        long testTimestamp  = timestamp(testTime, false);
        long minTimestamp   = timestamp(minTime, true);
        long maxMiliseconds = testTimestamp / Integer.parseInt(questions);
        
        if (maxMiliseconds < (minTimestamp + (minTimestamp * 0.5))) {
            JOptionPane.showMessageDialog(null, "Número de questões e tempo de prova não são relativos,\n pois cada questão deve ter pelo menos 50% do tempo mínimo ("+ minTime +") para resposta.", "Timer", JOptionPane.INFORMATION_MESSAGE);
            
            return;
        }
        
        toggleTimer(false);
        
        questionNumber = Integer.parseInt(questions);
        remainingMiliseconds = testTimestamp;
        endMiliseconds = testTimestamp;
        minQuestionMiliseconds = minTimestamp;
        
        lblRemainingTime.setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(remainingMiliseconds),
            TimeUnit.MILLISECONDS.toMinutes(remainingMiliseconds) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(remainingMiliseconds) % TimeUnit.MINUTES.toSeconds(1))
        );
        
        lblEndTime.setText(lblRemainingTime.getText());
        
        (remainingThread = remainingTimer()).start();
        (endThread = endTimer()).start();
        
        Answer answer = new Answer();
        
        answer.setQuestion(1);
        answer.setAnswerMiliseconds(0);
        answer.setMaxMiliseconds(maxMiliseconds);
        answer.setAnswered(false);
        
        answers.add(answer);
        (questionThread = questionTimer(answer)).start();
        
        lblOption.setText("Alternativas da Questão: " + answer.getQuestion());
        lblMaxTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(answer.getMaxMiliseconds()) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(answer.getMaxMiliseconds()) % TimeUnit.MINUTES.toSeconds(1))
        );
    }//GEN-LAST:event_btnBeginActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Deseja parar o timer?", "", JOptionPane.YES_NO_OPTION);
        
        if (confirm == 1) return;
        
        long remainingTime = remainingMiliseconds;
        
        toggleTimer(true);
        
        if (answers.size() > 1) {
            try {
                Peformance.render(answers, remainingTime);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Não foi possível gerar o relatório:\n"  + ex.getMessage(), "Timer", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        answers.clear();
    }//GEN-LAST:event_btnStopActionPerformed

    private void btnNextQuestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextQuestionActionPerformed
        Answer lastAnswer = answers.get(answers.size() - 1);
        
        if (lastAnswer.getAnswerMiliseconds() < minQuestionMiliseconds) {
            JOptionPane.showMessageDialog(null, "Tempo mínimo de resposta é "+ txtMinQuestionTime.getText() +".", "Timer", JOptionPane.INFORMATION_MESSAGE);
            
            return;
        }

        String option = questionGroup.getSelection().getActionCommand();
        
        lastAnswer.setOption(option);
        lastAnswer.setAnswered(true);
        
        if (answers.size() == questionNumber) {
            long remainingTime = remainingMiliseconds;
            
            toggleTimer(true);
            
            try {
                Peformance.render(answers, remainingTime);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Não foi possível gerar o relatório:\n"  + ex.getMessage(), "Timer", JOptionPane.ERROR_MESSAGE);
            }
            
            answers.clear();
            
            return;
        }
        
        questionThread.interrupt();
        
        lblLatestTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(lastAnswer.getAnswerMiliseconds()) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(lastAnswer.getAnswerMiliseconds()) % TimeUnit.MINUTES.toSeconds(1))
        );
        
        long extraSum = answers.stream().mapToLong(a -> a.getExtraMiliseconds()).sum();
        int answered = questionNumber - answers.size();
        
        if (extraSum < 0) {
            endMiliseconds = remainingMiliseconds + ((remainingMiliseconds - (extraSum / answered)) / answered);
        } else {
            endMiliseconds = remainingMiliseconds - ((remainingMiliseconds - (extraSum / answered)) / answered);
        }
        
        String symbol = lastAnswer.getExtraMiliseconds() == 0 ? "" : (lastAnswer.getExtraMiliseconds() > 0 ? "+" : "-");
        Color color = lastAnswer.getExtraMiliseconds() == 0 ? new Color(40, 40, 40) : (lastAnswer.getExtraMiliseconds() > 0 ? new Color(30, 130, 76) : new Color(240, 52, 52));
        long  extraMiliseconds = lastAnswer.getExtraMiliseconds() < 0 ? lastAnswer.getExtraMiliseconds() * -1 : lastAnswer.getExtraMiliseconds();        
        
        lblPenaltyAndExtraTime.setForeground(color);
        lblPenaltyAndExtraTime.setText(symbol + String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(extraMiliseconds) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(extraMiliseconds) % TimeUnit.MINUTES.toSeconds(1))
        );
        
        Answer nextAnswer = new Answer();
        
        nextAnswer.setQuestion(answers.size() + 1);
        nextAnswer.setAnswerMiliseconds(0);
        nextAnswer.setMaxMiliseconds(remainingMiliseconds / (questionNumber - answers.size()));
        nextAnswer.setAnswered(false);
        
        answers.add(nextAnswer);
        (questionThread = questionTimer(nextAnswer)).start();
        
        lblOption.setText("Alternativas da Questão: " + nextAnswer.getQuestion());
        lblMaxTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(nextAnswer.getMaxMiliseconds()) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(nextAnswer.getMaxMiliseconds()) % TimeUnit.MINUTES.toSeconds(1))
        );
        
        if (answers.size() == questionNumber) btnNextQuestion.setText("Finalizar Simulado");
    }//GEN-LAST:event_btnNextQuestionActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Timer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(() -> {
            new Timer().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBegin;
    private javax.swing.JButton btnNextQuestion;
    private javax.swing.JButton btnStop;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblEndTime;
    private javax.swing.JLabel lblLatestTime;
    private javax.swing.JLabel lblMaxTime;
    private javax.swing.JLabel lblOption;
    private javax.swing.JLabel lblPenaltyAndExtraTime;
    private javax.swing.JLabel lblQuestionTime;
    private javax.swing.JLabel lblRemainingTime;
    private javax.swing.ButtonGroup questionGroup;
    private javax.swing.JRadioButton radioFifthOption;
    private javax.swing.JRadioButton radioFirstOption;
    private javax.swing.JRadioButton radioFourthOption;
    private javax.swing.JRadioButton radioSecondOption;
    private javax.swing.JRadioButton radioThirdOption;
    private javax.swing.JFormattedTextField txtHours;
    private javax.swing.JFormattedTextField txtMinQuestionTime;
    private javax.swing.JTextField txtQuestions;
    // End of variables declaration//GEN-END:variables
}
