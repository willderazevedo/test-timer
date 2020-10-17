package view;

import filter.IntegerFilter;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Image;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.text.PlainDocument;
import model.Answer;
import report.Peformance;

public class Timer extends javax.swing.JFrame {

    private int questionNumber;
    private long remainingMiliseconds;
    private long endMiliseconds;
    private long minQuestionMiliseconds;
    private long maxMiliseconds;
    private long currentMiliseconds;
    private Thread remainingThread = null;
    private Thread questionThread = null;
    private Thread endThread = null;
    private Thread maxThread = null;
    private Thread currentThread = null;
    private final List<Answer> answers = new ArrayList<>();
    
    public Timer() {
        initComponents();
        setLocationRelativeTo(null);
        setIconImage(new javax.swing.ImageIcon("assets/logo.png").getImage());
        
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
                        toggleTimer(true);
                        JOptionPane.showMessageDialog(null, "Tempo esgotado", "Temporizador de Provas / Simulados", JOptionPane.INFORMATION_MESSAGE);
                        
                        if (answers.size() > 1) {
                            try {
                                Peformance.render(answers, 0, currentMiliseconds);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Não foi possível gerar o relatório:\n"  + ex.getMessage(), "Temporizador de Provas / Simulados", JOptionPane.ERROR_MESSAGE);
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
                        lblQuestionTime.setText("00:00:00");
                        lblQuestionTime.setForeground(new Color(40, 40, 40));

                        break;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        lblQuestionTime.setText("00:00:00");
                        lblQuestionTime.setForeground(new Color(40, 40, 40));
                        
                        break;
                    }
                    
                    answer.setAnswerMiliseconds(answer.getAnswerMiliseconds() + 1000);
                    answer.setExtraMiliseconds(answer.getMaxMiliseconds() - answer.getAnswerMiliseconds());

                    if (answer.getExtraMiliseconds() < 0 && !answer.isPenalty()) {
                        answer.setPenalty(true);
                        lblQuestionTime.setForeground(new Color(240, 52, 52));
                    }

                    lblQuestionTime.setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(answer.getAnswerMiliseconds()),
                        TimeUnit.MILLISECONDS.toMinutes(answer.getAnswerMiliseconds()) % TimeUnit.HOURS.toMinutes(1),
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
                while (endMiliseconds > 0 || !Thread.interrupted()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        break;
                    }

                    endMiliseconds -= 1000;    
                    
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
    
    private Thread maxTimer() {
        return new Thread ()  {
            @Override
            public void run() {
                while (maxMiliseconds >= 0 || !Thread.interrupted()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        break;
                    }

                    maxMiliseconds -= 1000;    

                    lblMaxTime.setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(maxMiliseconds),
                        TimeUnit.MILLISECONDS.toMinutes(maxMiliseconds) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(maxMiliseconds) % TimeUnit.MINUTES.toSeconds(1))
                    );
                }
            }
        };
    }
    
    private Thread currentTimer() {
        return new Thread ()  {
            @Override
            public void run() {
                while (true){
                    if (Thread.interrupted()) break;
                    
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        break;
                    }

                    currentMiliseconds += 1000;    

                    lblCurrentTime.setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(currentMiliseconds),
                        TimeUnit.MILLISECONDS.toMinutes(currentMiliseconds) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(currentMiliseconds) % TimeUnit.MINUTES.toSeconds(1))
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
        jLabel12 = new javax.swing.JLabel();
        lblCurrentTime = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblTotalPositive = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblTotalNegative = new javax.swing.JLabel();
        lblLogo = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel17 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lblLink = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();

        jButton2.setText("jButton2");

        jLabel15.setText("jLabel15");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Temporizador de Provas / Simulados");

        jLabel1.setText("Número Questões");

        jLabel2.setText("Tempo de Prova");

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

        jLabel5.setText("Tempo decorrido");

        lblMaxTime.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblMaxTime.setForeground(new java.awt.Color(40, 40, 40));
        lblMaxTime.setText("00:00:00");

        jLabel7.setText("Tempo máximo");

        lblQuestionTime.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblQuestionTime.setForeground(new java.awt.Color(40, 40, 40));
        lblQuestionTime.setText("00:00:00");

        jLabel9.setText("Acréscimo/Penalidades");

        lblPenaltyAndExtraTime.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblPenaltyAndExtraTime.setForeground(new java.awt.Color(40, 40, 40));
        lblPenaltyAndExtraTime.setText("00:00:00");

        jLabel11.setText("Previsão de Término");

        lblEndTime.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblEndTime.setForeground(new java.awt.Color(40, 40, 40));
        lblEndTime.setText("00:00:00");

        jLabel13.setText("Último Tempo");

        lblLatestTime.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblLatestTime.setForeground(new java.awt.Color(40, 40, 40));
        lblLatestTime.setText("00:00:00");

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

        jLabel3.setText("Tempo mínimo de Resposta");

        jLabel12.setText("Tempo decorrido");

        lblCurrentTime.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblCurrentTime.setForeground(new java.awt.Color(40, 40, 40));
        lblCurrentTime.setText("00:00:00");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("Tempo Geral");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setText("Questão Atual");

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("Totais");

        jLabel14.setText("Total de Acréscimo");

        lblTotalPositive.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblTotalPositive.setForeground(new java.awt.Color(30, 130, 76));
        lblTotalPositive.setText("00:00:00");

        jLabel16.setText("Total de Penalidade");

        lblTotalNegative.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblTotalNegative.setForeground(new java.awt.Color(240, 52, 52));
        lblTotalNegative.setText("00:00:00");

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/full-logo.png"))); // NOI18N
        lblLogo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblLogo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblLogoMouseClicked(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("Telefone:");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Website:");

        lblLink.setForeground(new java.awt.Color(0, 153, 255));
        lblLink.setText("http://solucaomendesgc.com.br");
        lblLink.setToolTipText("Solução Informática");
        lblLink.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblLinkMouseClicked(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel21.setText("(85) 3467-5731 - (85) 98899-1815");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addComponent(jSeparator3)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblRemainingTime)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(lblEndTime))
                        .addGap(61, 61, 61)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(lblCurrentTime)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblQuestionTime)
                                    .addComponent(jLabel5))
                                .addGap(29, 29, 29)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(lblMaxTime)))
                            .addComponent(jLabel8)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblLatestTime)
                                    .addComponent(jLabel13))
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(lblPenaltyAndExtraTime))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel10)
                            .addComponent(lblTotalPositive)
                            .addComponent(jLabel16)
                            .addComponent(lblTotalNegative))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnNextQuestion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblOption)
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addComponent(jSeparator5)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBegin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStop, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(txtQuestions, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtHours)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtMinQuestionTime, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblLogo)
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel17)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLink)
                            .addComponent(jLabel21))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(radioFirstOption)
                .addGap(18, 18, 18)
                .addComponent(radioSecondOption)
                .addGap(18, 18, 18)
                .addComponent(radioThirdOption)
                .addGap(18, 18, 18)
                .addComponent(radioFourthOption)
                .addGap(18, 18, 18)
                .addComponent(radioFifthOption)
                .addGap(83, 83, 83))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblLogo))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel21))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(lblLink))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtQuestions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMinQuestionTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBegin)
                    .addComponent(btnStop))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel12)
                            .addGap(6, 6, 6)
                            .addComponent(lblCurrentTime))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblRemainingTime)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblEndTime)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblLatestTime))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblMaxTime)
                                    .addComponent(lblQuestionTime))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblPenaltyAndExtraTime)))
                        .addGap(23, 23, 23))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTotalPositive)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTotalNegative))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                .addContainerGap())
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
        lblCurrentTime.setText("00:00:00");
        lblTotalPositive.setText("00:00:00");
        lblTotalNegative.setText("00:00:00");
        lblLatestTime.setText("00:00:00");
        lblMaxTime.setText("00:00:00");
        lblPenaltyAndExtraTime.setText("00:00:00");
        lblPenaltyAndExtraTime.setForeground(new Color(40, 40, 40));
        lblQuestionTime.setText("00:00:00");
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
            currentThread.interrupt();
            
            if (maxThread != null) maxThread.interrupt();
        }
    }
    
    private void btnBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBeginActionPerformed
        String questions = txtQuestions.getText();
        String testTime  = txtHours.getText().trim();
        String minTime   = txtMinQuestionTime.getText().trim();

        if (questions.isEmpty() || Integer.parseInt(questions) == 0 || testTime.equals(":") || minTime.equals(":")) {
            JOptionPane.showMessageDialog(this, "Preencha o número de questões, tempo de prova e o tempo mínimo para resposta.", "Temporizador de Provas / Simulados", JOptionPane.INFORMATION_MESSAGE);
            
            return;
        }
        
        long testTimestamp  = timestamp(testTime, false);
        long minTimestamp   = timestamp(minTime, true);
        long maxMiliseconds = testTimestamp / Integer.parseInt(questions);
        
        if (maxMiliseconds < (minTimestamp + (minTimestamp * 0.5))) {
            JOptionPane.showMessageDialog(this, "Número de questões e tempo de prova não são relativos,\n pois cada questão deve ter pelo menos 50% do tempo mínimo ("+ minTime +") para resposta.", "Temporizador de Provas / Simulados", JOptionPane.INFORMATION_MESSAGE);
            
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
        (currentThread = currentTimer()).start();
        
        Answer answer = new Answer();
        
        answer.setQuestion(1);
        answer.setAnswerMiliseconds(0);
        answer.setMaxMiliseconds(maxMiliseconds);
        answer.setAnswered(false);
        
        answers.add(answer);
        (questionThread = questionTimer(answer)).start();
        
        lblOption.setText("Alternativas da Questão: " + answer.getQuestion());
        lblMaxTime.setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(answer.getMaxMiliseconds()),
            TimeUnit.MILLISECONDS.toMinutes(answer.getMaxMiliseconds()) % TimeUnit.HOURS.toMinutes(1),
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
                Peformance.render(answers, remainingTime, currentMiliseconds);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Não foi possível gerar o relatório:\n"  + ex.getMessage(), "Temporizador de Provas / Simulados", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        answers.clear();
    }//GEN-LAST:event_btnStopActionPerformed

    private void btnNextQuestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextQuestionActionPerformed
        Answer lastAnswer = answers.get(answers.size() - 1);
        
        if (lastAnswer.getAnswerMiliseconds() < minQuestionMiliseconds) {
            JOptionPane.showMessageDialog(this, "Tempo mínimo de resposta é "+ txtMinQuestionTime.getText() +".", "Temporizador de Provas / Simulados", JOptionPane.INFORMATION_MESSAGE);
            
            return;
        }

        String option = questionGroup.getSelection().getActionCommand();
        
        lastAnswer.setOption(option);
        lastAnswer.setAnswered(true);
        
        if (answers.size() == questionNumber) {
            long remainingTime = remainingMiliseconds;
            
            toggleTimer(true);
            
            try {
                Peformance.render(answers, remainingTime, currentMiliseconds);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Não foi possível gerar o relatório:\n"  + ex.getMessage(), "Temporizador de Provas / Simulados", JOptionPane.ERROR_MESSAGE);
            }
            
            answers.clear();
            
            return;
        }
        
        questionThread.interrupt();
        
        lblLatestTime.setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(lastAnswer.getAnswerMiliseconds()),
            TimeUnit.MILLISECONDS.toMinutes(lastAnswer.getAnswerMiliseconds()) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(lastAnswer.getAnswerMiliseconds()) % TimeUnit.MINUTES.toSeconds(1))
        );
        
        int answered = questionNumber - answers.size();
        long endTime = lastAnswer.getExtraMiliseconds() < 0 ?
            remainingMiliseconds + (lastAnswer.getExtraMiliseconds() / answered) :
            remainingMiliseconds - (lastAnswer.getExtraMiliseconds() / answered)
        ;
        
        if (endTime > 0) endMiliseconds = endTime;

        String symbol = lastAnswer.getExtraMiliseconds() == 0 ? "" : (lastAnswer.getExtraMiliseconds() > 0 ? "+" : "-");
        Color color = lastAnswer.getExtraMiliseconds() == 0 ? new Color(40, 40, 40) : (lastAnswer.getExtraMiliseconds() > 0 ? new Color(30, 130, 76) : new Color(240, 52, 52));
        long  extraMiliseconds = lastAnswer.getExtraMiliseconds() < 0 ? lastAnswer.getExtraMiliseconds() * -1 : lastAnswer.getExtraMiliseconds();        
        
        lblPenaltyAndExtraTime.setForeground(color);
        lblPenaltyAndExtraTime.setText(symbol + String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(extraMiliseconds),
            TimeUnit.MILLISECONDS.toMinutes(extraMiliseconds) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(extraMiliseconds) % TimeUnit.MINUTES.toSeconds(1))
        );
        
        long totalPositive = answers.stream().filter(a -> a.getExtraMiliseconds() > 0).mapToLong(a -> a.getExtraMiliseconds()).max().orElse(0);
        long totalNegative = answers.stream().filter(a -> a.getExtraMiliseconds() < 0).mapToLong(a -> a.getExtraMiliseconds()).min().orElse(0) * -1;
        
        lblTotalPositive.setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totalPositive),
            TimeUnit.MILLISECONDS.toMinutes(totalPositive) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(totalPositive) % TimeUnit.MINUTES.toSeconds(1))
        );
        
        lblTotalNegative.setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totalNegative),
            TimeUnit.MILLISECONDS.toMinutes(totalNegative) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(totalNegative) % TimeUnit.MINUTES.toSeconds(1))
        );
        
        long maxTime = (remainingMiliseconds / (questionNumber - answers.size())) + lastAnswer.getExtraMiliseconds();
        
        if (maxTime > remainingMiliseconds) {
            maxTime = remainingMiliseconds;
            maxMiliseconds = maxTime;
            
            if (maxThread == null) (maxThread = maxTimer()).start();
        }
        
        Answer nextAnswer = new Answer();
        
        nextAnswer.setQuestion(answers.size() + 1);
        nextAnswer.setAnswerMiliseconds(0);
        nextAnswer.setMaxMiliseconds(maxTime);
        nextAnswer.setAnswered(false);
        
        answers.add(nextAnswer);
        (questionThread = questionTimer(nextAnswer)).start();
        
        lblOption.setText("Alternativas da Questão: " + nextAnswer.getQuestion());
        lblMaxTime.setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(nextAnswer.getMaxMiliseconds()),
            TimeUnit.MILLISECONDS.toMinutes(nextAnswer.getMaxMiliseconds()) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(nextAnswer.getMaxMiliseconds()) % TimeUnit.MINUTES.toSeconds(1))
        );
        
        if (answers.size() == questionNumber) btnNextQuestion.setText("Finalizar Simulado");
    }//GEN-LAST:event_btnNextQuestionActionPerformed

    private void lblLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLinkMouseClicked
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        
        if (desktop == null || !desktop.isSupported(Desktop.Action.BROWSE)) return;
        
        try {
            desktop.browse(new URI("https://solucaomendesgc.com.br"));
        } catch (URISyntaxException | IOException ex) {
            JOptionPane.showMessageDialog(this, "Não foi possível abrir o link:\n" + ex.getMessage(), "Temporizador de Provas / Simulados", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_lblLinkMouseClicked

    private void lblLogoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLogoMouseClicked
        lblLinkMouseClicked(evt);
    }//GEN-LAST:event_lblLogoMouseClicked

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
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JLabel lblCurrentTime;
    private javax.swing.JLabel lblEndTime;
    private javax.swing.JLabel lblLatestTime;
    private javax.swing.JLabel lblLink;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblMaxTime;
    private javax.swing.JLabel lblOption;
    private javax.swing.JLabel lblPenaltyAndExtraTime;
    private javax.swing.JLabel lblQuestionTime;
    private javax.swing.JLabel lblRemainingTime;
    private javax.swing.JLabel lblTotalNegative;
    private javax.swing.JLabel lblTotalPositive;
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
