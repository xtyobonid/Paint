import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;

//save the picture
//implement fill
//implement color dropper
//implement different brushes

public class Paint extends JFrame implements ActionListener {
	private JPanel topBar;
	private JPanel colorBox;
	private JPanel shapeBox;
	private JComboBox<String> brushSizes;
	private JButton[] colors;
	private JPanel inputOutput;
	private JTextField fileInput;
	private JButton save;
	private JButton[] shapes;
	private JButton brush;
	private DrawArea drawArea;
	private final Color PANEL_COLOR = new Color(245, 246, 247);
	
	public Paint() throws IOException {
		Scanner in = new Scanner(new File("ColorButtons.txt"));
		colors = new JButton[30];
		setTitle("Paint");
		setSize(800,800);
		setResizable(false);
		setLayout(new BorderLayout(10, 10));
		drawArea = new DrawArea();
		topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
		colorBox = new JPanel(new GridLayout(15, 3, 5, 5));
		for (int i = 0; i < colors.length; i++) {
			int r = in.nextInt();
			int g = in.nextInt();
			int b = in.nextInt();
			
			JButton buttonToAdd = new JButton();
			if (r == -1) {
				buttonToAdd.setBackground(PANEL_COLOR);
			} else {
				buttonToAdd.setBackground(new Color(r, g, b));
			}
			colors[i] = buttonToAdd;
			colors[i].addActionListener(this);
			colors[i].setActionCommand("Color$" + r + "%" + g + "&" + b + "*");
			colorBox.add(colors[i]);
			if (in.hasNextLine())
				in.nextLine();
		}
		//Invisible Buttons
		for (int j = 0; j < 15; j++) {
			JButton hidden = new JButton();
			hidden.setVisible(false);
			colorBox.add(hidden);
		}
		String[] sizes = {"1", "2", "3", "5", "8", "10", "25", "50", "100"};
		brushSizes = new JComboBox<String>(sizes);
		brushSizes.setSelectedIndex(0);
		brushSizes.setActionCommand("BrushSize");
		brushSizes.addActionListener(this);
		inputOutput = new JPanel(new BorderLayout(5,5));
		BufferedImage sv = ImageIO.read(new File("save.png"));
		save = new JButton(new ImageIcon(sv));
		save.addActionListener(this);
		save.setActionCommand("Save");
		fileInput = new JTextField();
		inputOutput.add(fileInput, BorderLayout.NORTH);
		inputOutput.add(save, BorderLayout.EAST);
		setLocationRelativeTo(null);
		shapeBox = new JPanel(new GridLayout(2, 4));
		shapes = new JButton[7];
		for (int i = 0; i < shapes.length; i++) {
			BufferedImage bg = ImageIO.read(new File("shape" + (i + 1) + ".png"));
			shapes[i] = new JButton(new ImageIcon(bg));
			shapes[i].addActionListener(this);
			shapes[i].setActionCommand("Shape" + (i + 1));
			shapeBox.add(shapes[i]);
		}
		BufferedImage inp = ImageIO.read(new File("brush.png"));
		brush = new JButton(new ImageIcon(inp));
		topBar.add(inputOutput);
		topBar.add(shapeBox);
		topBar.add(brushSizes);
		topBar.add(brush);
		brush.addActionListener(this);
		brush.setActionCommand("Brush");
		//topBar.add(colorBox);
		add(topBar, BorderLayout.NORTH);
		add(colorBox, BorderLayout.WEST);
		add(drawArea, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		in.close();
	}
	
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if (command.substring(0,4).equals("Save")) {
			if (fileInput.getText().contains(".png")) {
				try {
					ImageIO.write(drawArea.getImage(), "png", new File(fileInput.getText()));
				} catch (IOException e) {}
			}
		} else if (command.substring(0,5).equals("Color")) {
			int r = Integer.parseInt(command.substring(command.indexOf("$") + 1, command.indexOf("%")));
			int g = Integer.parseInt(command.substring(command.indexOf("%") + 1, command.indexOf("&")));
			int b = Integer.parseInt(command.substring(command.indexOf("&") + 1, command.indexOf("*")));
			
			if (r == -1) {
				Color chosen = JColorChooser.showDialog(this, "Colors", Color.BLACK);
				JButton tempButton = (JButton)event.getSource();
				tempButton.setBackground(chosen);
				tempButton.setActionCommand("Color$" + chosen.getRed() + "%" + chosen.getGreen() + "&" + chosen.getBlue() + "*");
				drawArea.setCurrentColor(chosen);
			} else {
				drawArea.setCurrentColor(new Color(r,g,b));
			}
		} else if (command.equals("BrushSize")) {
			drawArea.setBrushStroke(Integer.parseInt((String)brushSizes.getSelectedItem()));
		} else if (command.substring(0,5).equals("Shape")) {
			drawArea.setDrawType(command);
		} else if (command.substring(0,5).equals("Brush")) {
			drawArea.setDrawType("Brush");
		}
	}
	
	public static void main(String[] args) throws IOException {
		Paint paint = new Paint();
	}
}