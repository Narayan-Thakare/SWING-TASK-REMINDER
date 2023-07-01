package task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Task extends JFrame {

    private List<TaskReminder> tasks;
    private DefaultListModel<String> listModel;
    private JList<String> taskList;
    private TaskForm taskForm;
    private JLabel backgroundLabel;

    public Task() {
        setTitle("Task Reminder");
       setBounds(400, 300, 600, 400);
    //    setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tasks = new ArrayList<>();
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskForm = new TaskForm();
        backgroundLabel = new JLabel();

        taskForm.setAddButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String task = taskForm.getTask();
                LocalTime reminderTime = taskForm.getReminderTime();
                if (!task.isEmpty() && reminderTime != null) {
                    TaskReminder taskReminder = new TaskReminder(task, reminderTime);
                    tasks.add(taskReminder);
                    listModel.addElement(task);
                    taskForm.clearTaskField();
                }
            }
        });

        taskList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int index = taskList.locationToIndex(evt.getPoint());
                if (index >= 0 && index < tasks.size() && evt.getClickCount() == 2) {
                    TaskReminder taskReminder = tasks.get(index);
                    String editedTask = JOptionPane.showInputDialog(Task.this, "Edit task:", taskReminder.getTask());
                    if (editedTask != null && !editedTask.isEmpty()) {
                        taskReminder.setTask(editedTask);
                        listModel.setElementAt(editedTask, index);
                    }
                }
            }
        });

        startReminderTimer();

        setLayout(new BorderLayout());
        add(backgroundLabel, BorderLayout.CENTER);
        add(taskList, BorderLayout.CENTER);
        add(taskForm, BorderLayout.SOUTH);

        setBackgroundImage("D:\\HTML\for code.jpg"); // Set the background image

        setVisible(true);
    }

    private void setBackgroundImage(String imagePath) {
        ImageIcon imageIcon = new ImageIcon(imagePath);
        Image image = imageIcon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(image));
    }

    private void startReminderTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LocalTime currentTime = LocalTime.now();

                for (int i = 0; i < tasks.size(); i++) {
                    TaskReminder taskReminder = tasks.get(i);
                    LocalTime reminderTime = taskReminder.getReminderTime();
                    if (currentTime.getHour() == reminderTime.getHour() && currentTime.getMinute() == reminderTime.getMinute() && currentTime.getSecond() == reminderTime.getSecond()) {
                        int finalI = i;
                        SwingUtilities.invokeLater(() -> {
                            taskList.setSelectionBackground(Color.YELLOW);
                            taskList.setSelectedIndex(finalI);
                        });
                        Timer blinkTimer = new Timer();
                        blinkTimer.schedule(new TimerTask() {
                            boolean showBackground = false;

                            @Override
                            public void run() {
                                SwingUtilities.invokeLater(() -> {
                                    if (showBackground) {
                                        taskList.setSelectionBackground(Color.YELLOW);
                                    } else {
                                        taskList.setSelectionBackground(taskList.getBackground());
                                    }
                                    showBackground = !showBackground;
                                });
                            }
                        }, 0, 500); // Blink every 500 milliseconds
                        Timer stopBlinkTimer = new Timer();
                        stopBlinkTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                blinkTimer.cancel();
                                SwingUtilities.invokeLater(() -> {
                                    taskList.setSelectionBackground(taskList.getBackground());
                                });
                            }
                        }, 3000); // Stop blinking after 3 seconds
                    }
                }
            }
        }, 0, 1000);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Task();
        });
    }
}

class TaskForm extends JPanel {

    private JTextField taskField;
    private JTextField reminderField;
    private JButton addButton;
////////////////////////////////////////////////////////////////////////////////////////
    public TaskForm() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        taskField = new JTextField();
        taskField.setFont(getFont());
        reminderField = new JTextField(8);
        reminderField.setFont(new Font("Algerian", Font.BOLD, 35));

        addButton = new JButton("Add");

        add(new JLabel("Task: "));
        add(taskField);
        add(new JLabel("Reminder (HH:MM): "));
        add(reminderField);
        add(addButton);
        addButton.setForeground(Color.blue);
        addButton.setFont(new Font("Algerian", Font.BOLD, 35));

    }

    public String getTask() {
        return taskField.getText();
    }

    public LocalTime getReminderTime() {
        String reminderTime = reminderField.getText();
        try {
            return LocalTime.parse(reminderTime);
        } catch (Exception e) {
            return null;
        }
    }

    public void clearTaskField() {
        taskField.setText("");
        reminderField.setText("");
    }

    public void setAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }
}

class TaskReminder {
    private String task;
    private LocalTime reminderTime;

    public TaskReminder(String task, LocalTime reminderTime) {
        this.task = task;
        this.reminderTime = reminderTime;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public LocalTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalTime reminderTime) {
        this.reminderTime = reminderTime;
    }
}
