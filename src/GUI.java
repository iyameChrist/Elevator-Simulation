import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
//import java.sql.DriverManager;
import static java.awt.BorderLayout.*;

public class GUI extends JFrame{
    int currentFloor = 0;
    int nextFloor = 0;
    int[] movesArr = new int[] {0,1,2,3,4,5,6};
    int numberOfButtons = 7;
    Animation anime = new Animation();
    JFrame frame = new JFrame("ELEVATOR SIMULATION");
    JPanel buttonPanel = new JPanel();
    GridLayout grid = new GridLayout(0, 3) {{ setHgap(5); setVgap(5); }};
    JButton[] button = new JButton[numberOfButtons];
    JButton open, close, up, down, up_arrow,down_arrow;
    boolean closed = true;
    boolean opened = true;
    Listener listener = new Listener();
    Color buttonColor;
    Connection connect;
    String sql = "SELECT NAME FROM FLOORS";
    Statement stmt;
    ResultSet rs;



    public GUI(Connection conn) {

        connect = conn;
        try{
            stmt = connect.createStatement();
            rs    = stmt.executeQuery(sql);
        }catch(SQLException e){

        }
        //Setup frame
        frame.setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.Y_AXIS));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //Create button panel
        buttonPanel.setLayout(grid);
        buttonPanel.setPreferredSize(new Dimension(1, 1));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addButtonPanel();

    }


    public void centerFrame() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((dim.width - frame.getWidth()) / 2,
                (dim.height - frame.getHeight()) / 2);
    }


    public void addButtonPanel() {

        for (int i=0; i < button.length; i++){
            try{
                rs.next();
                button[i] = new JButton();
                button[i].addActionListener(listener.new ButtonListener());
                button[i].setText(rs.getString("NAME"));
                button[i].setName(Integer.toString(i));
                buttonPanel.add(button[i]);
            }catch(SQLException e){

            }
        }
        open = new JButton();
        open.addActionListener(listener.new ButtonListener());
        open.setText("OPEN");
        buttonPanel.add(open);

        close = new JButton();
        close.addActionListener(listener.new ButtonListener());
        close.setText("CLOSE");
        buttonPanel.add(close);


        up = new JButton();
        up.addActionListener(listener.new ButtonListener());
        up.setText("UP");
        //anime.add(BorderLayout.WEST, up);
        //frame.getContentPane().add(BorderLayout.NORTH, up);


        down = new JButton();
        down.addActionListener(listener.new ButtonListener());
        down.setText("DOWN");
        //anime.add(WEST, down);
        //frame.getContentPane().add(BorderLayout.NORTH, down);


        up_arrow = new JButton();
        up_arrow.add(new BasicArrowButton(BasicArrowButton.NORTH));
        anime.add(WEST, up_arrow);

        down_arrow = new JButton();
        down_arrow.add(new BasicArrowButton(BasicArrowButton.SOUTH));
        anime.add(WEST, down_arrow);


        buttonColor = button[0].getBackground();
    }


    public void createGUI(){
        frame.add(up, WEST);
        frame.add(down, WEST);
        frame.getContentPane().add(BorderLayout.CENTER, anime);
        //frame.add(new JSeparator(),BorderLayout.SOUTH);
        frame.add(buttonPanel, SOUTH);

        frame.setPreferredSize(new Dimension(600, 730));
        frame.pack();
        centerFrame();
        buttonPanel.setVisible(true);
        frame.setVisible(true);
    }



    public class Listener {

        public class ButtonListener implements ActionListener {
            public void actionPerformed(ActionEvent event){
                final JButton cbutton = (JButton) event.getSource();
                Thread thread = new Thread(() -> {
                    OpenOrClose(cbutton);
                    BlinkThread(cbutton, Color.red);
                });
                thread.start();

                frame.repaint();
            }
        }

    }

    private void BlinkThread(JButton currentButton, Color color){
        ArrayList<Integer> movement = UpOrDown(currentButton);
        if (movement.size() > 1 ){
            for(int i: movement){
                anime.close();
                if(button[i]==currentButton){
                    try{
                        System.out.print("\n<--[ Destined Floor: "+i+" ]-->\n");
                        Thread.sleep(400);
                        button[i].setBackground(Color.green);
                        currentFloor = i++;
                        //anime.open();
                        break;
                    }catch(Exception e){

                    }
                }

                try{
                    Thread.sleep(400);
                    button[i].setBackground(color);
                    System.out.print("\n"+i+"\n");

                    Thread.sleep(400);
                    button[i].setBackground(buttonColor);
                    currentFloor = i++;
                }catch(Exception ex){
                    System.out.println("There is an error ooo!");
                }
            }
        }
    }

    private void OpenOrClose(JButton currentButton){
        if(open == currentButton){
            anime.open();
        }
        if(up == currentButton){
            anime.open();
        }
        if(down == currentButton){
            anime.open();
        }
        if(close == currentButton){
            anime.close();
        }
    }

    private ArrayList UpOrDown(JButton cb){
        int[] movement = new int[] {};
        ArrayList<Integer> move = new ArrayList<>();
        for(int i: movesArr){
            if(button[i]==cb){
                nextFloor = i;
                break;
            }
        }
        if(nextFloor > currentFloor){
            for(int i = currentFloor; i <= nextFloor; i++){
                move.add(i);
            }
            down_arrow.setBackground(buttonColor);
            up_arrow.setBackground(Color.cyan);
            System.out.println("Moving Up");
        }else if (nextFloor < currentFloor){
            for(int i = currentFloor; i >= nextFloor; i--){
                move.add(i);
            }
            System.out.println("Moving Down");
            up_arrow.setBackground(buttonColor);
            down_arrow.setBackground(Color.cyan);
        }
        return move;
    }

    public class Animation extends JPanel{

        private int x = 150, y = 300, vel = 2;

        public Animation(){
            setVisible(true);
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            //LEFT DOOR
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x, 55, 152, 270);
            //left.start();

            // RIGHT DOOR
            g.setColor(Color.DARK_GRAY);
            g.fillRect(y, 55, 152, 270);
            //right.start();

            // LEFT STRUCTURE
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, 150, 330);

            // RIGHT STRUCTURE
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(450, 0, 150, 330);

            String s = String.valueOf(nextFloor);
            // TOP STRUCTURE
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(150, 0, 300, 60);
            g.setColor(Color.RED);
            g.drawString(s,300, 60);

            // DOWN STRUCTURE
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 300, 600, 30);

        }

        public void open(){
            while(closed){
                try{
                    Thread.sleep(50);
                    if(x >= 0){
                        x -= vel;
                    }
                    repaint();

                    if (y <= 450){
                        y += vel;
                    }
                    repaint();

                    if (x <= 1){
                        opened = true;
                        closed = false;
                    }
                }catch(Exception e){

                }
            }
        }

        public void close(){
            while(opened){
                try{
                    Thread.sleep(50);
                    if(x <= 146){
                        x += vel;
                    }
                    repaint();

                    if (y >= 300){
                        y -= vel;
                    }
                    repaint();

                    if(y <= 301){
                        closed = true;
                        opened = false;
                    }
                }catch(Exception e){

                }
            }


        }
    }
}
