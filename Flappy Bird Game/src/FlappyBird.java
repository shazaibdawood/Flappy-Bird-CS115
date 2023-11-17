import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class FlappyBird implements ActionListener, MouseListener, KeyListener
{

	public static FlappyBird flappyBird;

	public final int WIDTH = 800, HEIGHT = 800;
		//Dimensions of GUI

	public Renderer renderer;
		//Renderer instance for graphics

	public Rectangle bird;

	public ArrayList<Rectangle> columns;
		//Arraylist for storing columns

	public int ticks, yMotion, score;

	public boolean gameOver, started;

	public Random rand;

	JFrame jframe = new JFrame();
	//Game window
	Timer timer = new Timer(20, this);
	public FlappyBird()
	{
		renderer = new Renderer();
		rand = new Random();

		jframe.add(renderer);
		jframe.setTitle("Flappy Bird");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(WIDTH, HEIGHT);
		jframe.addMouseListener(this);
		jframe.addKeyListener(this);
		jframe.setResizable(false);
		jframe.setVisible(true);

		bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
			//Drawing the bird
		columns = new ArrayList<Rectangle>();

			//Initial columns
		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);

		timer.start();
	}

		//Adding columns
	public void addColumn(boolean start)
	{
		int space = 300;
		int width = 100;
		int height = 10 + rand.nextInt(470);

		if (start)
		{
			columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));
			columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));
		}
		else
		{
			columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
			columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
		}
	}

		//Displaying column on screen
	public void paintColumn(Graphics g, Rectangle column)
	{
		g.setColor(Color.green.darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}

		//Moving the bird
	public void jump()
	{		//Setup the start screen
		if (gameOver)
		{
			bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
			columns.clear();
			yMotion = 0;
			score = 0;

			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);

			gameOver = false;
		}
			//Starting the game if not started
		if (!started)
		{
			started = true;
		}
		else if (!gameOver)
		{
			if (yMotion > 0)
			{
				yMotion = 0;
			}
				//Bird is always moving downwards
			yMotion -= 14;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		int speed = 10;

		ticks++;

		if (started)
		{		//Keeps columns moving left
			for (int i = 0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);

				column.x -= speed;
			}

			if (ticks % 2 == 0 && yMotion < 15)
			{
				yMotion += 3;
			}

				//Removes columns that are outside GUI boundaries
			for (int i = 0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);

				if (column.x + column.width < 0)
				{
					columns.remove(column);

					if (column.y == 0)
					{
						addColumn(false);
					}
				}
			}

				//moves the bird up and down
			bird.y += yMotion;

				//Checking if bird has hit a column
			for (Rectangle column : columns)
			{
				if (!gameOver && column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - speed && bird.x + bird.width / 2 < column.x + column.width / 2 + speed)
				{
					score++;
				}

				if (column.intersects(bird))
				{
					gameOver = true;

					if (bird.x <= column.x)
					{
						bird.x = column.x - bird.width;

					}
					else
					{
						if (column.y != 0)
						{
							bird.y = column.y - bird.height;
						}
						else if (bird.y < column.height)
						{
							bird.y = column.height;
						}
					}
				}
			}

				//Checks if bird hits top or ground
			if (bird.y > HEIGHT - 120 || bird.y < 0)
			{
				gameOver = true;
			}

			if (bird.y + yMotion >= HEIGHT - 120)
			{
				bird.y = HEIGHT - 120 - bird.height;
				gameOver = true;
			}
		}
			//Updates renderer
		renderer.repaint();
	}

		//Displaying updated game screens
	public void repaint(Graphics g)
	{
			//Background
		g.setColor(Color.cyan);
		g.fillRect(0, 0, WIDTH, HEIGHT);
			//ground
		g.setColor(Color.orange);
		g.fillRect(0, HEIGHT - 120, WIDTH, 120);

		g.setColor(Color.green);
		g.fillRect(0, HEIGHT - 120, WIDTH, 20);
			//bird
		g.setColor(Color.red);
		g.fillRect(bird.x, bird.y, bird.width, bird.height);
			//columns
		for (Rectangle column : columns)
		{
			paintColumn(g, column);
		}
			//On Screen messages
		Font font1 = new Font("Arial", Font.PLAIN, 50);
		Font font2 = new Font("Courier New", Font.BOLD, 100);
		Font font3 = new Font("Courier New", Font.BOLD, 125);
		g.setColor(Color.black);

		if (!started)
		{		//Start screen message
			g.setFont(font1);
			g.drawString("Press Space to Start!", 155, HEIGHT / 2 + 100);
		}

		if (gameOver)
		{		//Game over screen
				//columns keep moving and score is displayed
			bird = new Rectangle();
			g.setFont(font2);
			if (score >= 10) {
				g.drawString(String.valueOf(score), WIDTH / 2 - 35, 100);
			} else {
				g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
			}
			g.setFont(font3);
			g.drawString("GAME OVER", 60, HEIGHT / 2 - 50);
			g.setFont(font1);
			g.drawString("Press Space to Restart", 145, HEIGHT / 2 + 100);

		}

		if (!gameOver && started)
		{	//Continuously displays score
			g.setFont(font2);
			if (score >= 10) {
				g.drawString(String.valueOf(score), WIDTH / 2 - 35, 100);
			} else {
				g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
			}

		}
	}

	public static void main(String[] args)
	{
		flappyBird = new FlappyBird();
	}


		//Bird jumps when mouse is clicked
	@Override
	public void mouseClicked(MouseEvent e)
	{
		//jump();
	}

		//Bird jumps when spacebar is pressed
	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			jump();
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{

	}

}
