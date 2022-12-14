package gpjecc.blogspot.com;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class FroggerGame extends ApplicationAdapter {

	private SpriteBatch batch;
	private OrthographicCamera camera;
	private BitmapFont font;
	private InitScreen initScreen;
	private Array<Movel> veiculos;
	private Movel movel;
	private Movel sapo;
	private Frogg sapoGameWin;
	private Random rand; // Todos objetos que serão utilizados no game
	private Texture frogg;
	private Texture carBlue;
	private Texture carGreen;
	private Texture carYellow;
	private Texture truck;
	private Texture truckBombeiro;
	private Texture road;
	private Texture Wall;
	private Texture Finish;
	private Texture health;
	private Sound gameOverSound;
	private Sound victorySound;
	private Music backgroundMusic;

	public int fase = 1;
	public int jaSorteado = 6;
	public int auxiliarFecharTela = 1;
	public int tempo = 40;
	public boolean game2 = false;
	public boolean game2ChegouNoFinal = false; // Variáveis utilizadas no game
	public boolean setMusicOneTime = false;
	public boolean gameWin1 = false;
	public boolean gameWin2 = false;
	private boolean gameOver = false;
	private boolean gameOver2 = false;
	private int seta1Vez = 0;
	private long lastDropTime;
	private int evitaSpawnMesmoLugar = 0;
	private long lastDropTime2;
	private int primeiraContagem = 0;

	private void carregaTexturas() { // Função que carrega as texturas
		road = new Texture(Gdx.files.internal("assets/estrada.png"));
		Wall = new Texture(Gdx.files.internal("assets/muro.png"));
		Finish = new Texture(Gdx.files.internal("assets/linhaChegada.png"));
		frogg = new Texture(Gdx.files.internal("assets/sapo.png"));
		carBlue = new Texture(Gdx.files.internal("assets/CarroAzul.png"));
		carGreen = new Texture(Gdx.files.internal("assets/carroVerde.png"));
		carYellow = new Texture(Gdx.files.internal("assets/carroAmarelo.png"));
		truck = new Texture(Gdx.files.internal("assets/caminhao.png"));
		truckBombeiro = new Texture(Gdx.files.internal("assets/caminhaoDeBombeiro.png"));
		health = new Texture(Gdx.files.internal("assets/vida.png"));
	}

	private void carregaMusicas() { // Função que carrega as músicas
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/musicaFundo.mp3"));
		gameOverSound = Gdx.audio.newSound(Gdx.files.internal("assets/gameover.mp3"));
		victorySound = Gdx.audio.newSound(Gdx.files.internal("assets/vitoria.mp3"));
	}

	private void setaMusicaFundo() { // Função que seta a música de fundo e já inicia ela
		backgroundMusic.setLooping(true);
		backgroundMusic.play();
	}

	private void carregaFontes() { // Função que carrega as fontes
		font = new BitmapFont();
		font.setScale(2);
	}

	private void setaDimensaoTela() { // Função que seta a dimensão da tela e cria um objeto da Classe SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
	}

	@Override
	public void create() { // Função que será chamada quando o jogo for iniciado

		Texture.setEnforcePotImages(false);

		rand = new Random(); // Cria um objeto da Classe Random que será utilizado dentro de algumas funções

		carregaTexturas();
		carregaMusicas();

		setaMusicaFundo(); // CHAMADA DAS FUNÇÕES

		setaDimensaoTela();
		carregaFontes();

		iniciaRodovia();

		spawnFrogg();

		veiculos = new Array<Movel>(); // Cria um Array de objetos da Classe Movel

		spawnVehicles(); // Chamada da função que spawnará somente uma vez o veículo

	}

	private void iniciaRodovia() { // Função que inicia a rodovia e cria as dimensões da mesma
		Rectangle rectangle = new Rectangle();
		rectangle.x = 0;
		rectangle.y = 70;
		rectangle.height = 225;
		rectangle.width = 800;
		initScreen = new Road(rectangle, road, null);
	}

	private void contagemTempo() { // Função que permite a contagem do tempo
		if (primeiraContagem == 0) {
			font.draw(batch, "TEMPO: " + tempo-- + " SEG", 270, 460);
			font.draw(batch, "TEMPO: " + tempo + " SEG", 270, 460);
			primeiraContagem++;
			lastDropTime2 = TimeUtils.nanoTime();
		}
		if (TimeUtils.nanoTime() - lastDropTime2 >= 1000000000) {
			font.setColor(Color.YELLOW);
			if (tempo > 0)
				font.draw(batch, "TEMPO: " + tempo-- + " SEG", 270, 460);
			lastDropTime2 = TimeUtils.nanoTime();
		}
		if (TimeUtils.nanoTime() - lastDropTime2 < 1000000000) {
			font.setColor(Color.YELLOW);
			font.draw(batch, "TEMPO: " + tempo + " SEG", 270, 460);
		}

	}

	private int sorteiaPosiçãoYDoVeiculo() { // Função que sorteia a posição Y do veículo que será spawnado
		int numeros[] = new int[] { 80, 140, 250, 190 };

		int indice;
		Random random = new Random();
		indice = random.nextInt(numeros.length);

		while (evitaSpawnMesmoLugar == indice) {
			indice = random.nextInt(numeros.length);
		}
		evitaSpawnMesmoLugar = indice;
		return numeros[indice];
	}

	private void spawnVehicles() { // Função que criará os veículos aleatoriamente
		int sorteio = rand.nextInt(5);
		Rectangle rectangle = new Rectangle();
		rectangle.y = sorteiaPosiçãoYDoVeiculo();
		if (jaSorteado == sorteio) {
			spawnVehicles();
		}
		if (sorteio == 0) {
			rectangle.width = carBlue.getWidth() * 0.5f;
			rectangle.height = carBlue.getHeight() * 0.5f;
			movel = new Veiculos(rectangle, carBlue, null);
		} else if (sorteio == 1) {
			rectangle.width = carGreen.getWidth() * 0.5f;
			rectangle.height = carGreen.getHeight() * 0.5f;
			movel = new Veiculos(rectangle, carGreen, null);
		} else if (sorteio == 2) {
			rectangle.width = carYellow.getWidth() * 0.5f;
			rectangle.height = carYellow.getHeight() * 0.5f;
			movel = new Veiculos(rectangle, carYellow, null);
		} else if (sorteio == 3) {
			rectangle.width = truck.getWidth() * 0.5f;
			rectangle.height = truck.getHeight() * 0.5f;
			movel = new Veiculos(rectangle, truck, null);
		} else {
			rectangle.width = truckBombeiro.getWidth() * 0.5f;
			rectangle.height = truckBombeiro.getHeight() * 0.5f;
			movel = new Veiculos(rectangle, truckBombeiro, null);
		}
		jaSorteado = sorteio;
		rectangle.x = 0 - rectangle.width;
		veiculos.add(movel); // Veículos criados adicionados no ArrayList da Classe Movel
		lastDropTime = TimeUtils.nanoTime();
	}

	private void spawnFrogg() { // Função que cria o Frogg
		Rectangle rectangle = new Rectangle();
		rectangle.x = 800 / 2 - 64 / 2;
		rectangle.y = 10;
		rectangle.width = frogg.getWidth();
		rectangle.height = frogg.getHeight();
		sapo = new Frogg(rectangle, frogg, null);
	}

	private void setMusic() { // Função que seta a música e coloca pra tocar de derrota ou vitória
		backgroundMusic.stop();
		if (gameOver && setMusicOneTime == false) {
			setMusicOneTime = true;
			gameOverSound.play();
		} else if (gameWin2 && setMusicOneTime == false) {
			setMusicOneTime = true;
			victorySound.play();
		}
	}

	private void carregaInitialElements() { // Função que desenha os elementos iniciais da tela como fontes e texturas
		if (fase == 1) {
			font.draw(batch, "FASE 1", 670, 460);
		} else if (fase == 2) {
			font.draw(batch, "FASE 2", 670, 460);
		}
		if (fase == 2 && seta1Vez == 0) {
			seta1Vez++;
			tempo = 40;
		}
		batch.draw(road, initScreen.getmRectangle().x, initScreen.getmRectangle().y, initScreen.getmRectangle().width,
				initScreen.getmRectangle().height);
		batch.draw(Wall, 0, 0, 800, 70);
		batch.draw(Finish, 0, 70 + initScreen.getmRectangle().height, 800, 50);
		batch.draw(sapo.getmImage(), sapo.getmRectangle().x, sapo.getmRectangle().y);
		for (Movel veiculo : veiculos) {
			batch.draw(veiculo.mImage, veiculo.getmRectangle().x, veiculo.getmRectangle().y,
					veiculo.getmRectangle().width, veiculo.getmRectangle().height);
		}
		font.setColor(Color.GREEN);
		font.draw(batch, String.valueOf(((Frogg) sapo).lives), 70, 460);
		batch.draw(health, 10, 460 - 40, 50, 50);
	}

	private void contagemTempoFase1() {// Função que conta o tempo da fase 1
		if (!(gameOver) && !(gameWin1)) {
			contagemTempo();
		}
	}

	private void acabouOJogoOuPassouDeFase() { // Função que verifica se o jogo acabou ou passou de fase
		if (fase == 2 && !gameOver2 && seta1Vez == 1) {
			if (Gdx.input.isKeyPressed(Keys.ENTER)) {
				if (gameWin2 || tempo == 0) {
					Gdx.app.exit();
				} else {
					((Frogg) sapo).setPosition(800 / 2 - 64 / 2, 10);
					((Frogg) sapo).lives = 1;
					gameWin1 = false;
					game2 = true;
				}
			}
			if (game2 && seta1Vez == 1 && !gameWin2) {
				contagemTempo();
			}
			if (gameWin2) {
				tempo = 40;
			}
		}
	}

	private void verificaVenceuOuPerdeu() { // Função que verifica se o jogador venceu, passou de fase ou perdeu e seta
											// os valores correspondentes
		if (gameWin1 || (!gameWin2 && (game2)) || gameWin2) {
			font.setColor(Color.CYAN);
			if (gameWin1)
				font.draw(batch, "VOCÊ VENCEU!", 800 / 2 - 150, 250);
			if (gameWin2) {
				setMusic();
				font.draw(batch, "VOCÊ VENCEU O JOGO!", 800 / 2 - 200, 250);
				font.draw(batch, "PRESSIONE ENTER PARA SAIR", 800 / 2 - 250, 200);
			} else {
				font.draw(batch, "PRESSIONE ENTER PARA CONTINUAR", 800 / 2 - 310, 200);
			}
			if (fase == 1 && gameWin1 == true) {
				fase = 2;
				gameWin2 = false;
				sapoGameWin.setgameWin2(gameWin2);
			} else if (fase == 2) {
				game2 = false;
				sapoGameWin.setgameWin2(gameWin2);
			}
		} else if (((Frogg) sapo).lives == 0 && auxiliarFecharTela == 1 || tempo == 0) {
			font.setColor(Color.RED);
			font.draw(batch, "VOCÊ PERDEU!", 800 / 2 - 150, 250);
			font.draw(batch, "PRESSIONE ENTER PARA FECHAR O PROGRAMA", 800 / 2 - 360, 200);
			gameOver = true;
			sapoGameWin.setgameOver(gameOver);
			setMusic();
			if (Gdx.input.isKeyPressed(Keys.ENTER))
				Gdx.app.exit();
		}
	}

	private void spawnVehiclesPeloTempo() { // Função que spawna os veículos de acordo com o tempo
		if (TimeUtils.nanoTime() - lastDropTime > 900000000) {
			spawnVehicles();
		}
	}

	@Override
	public void render() { // Função que será chamada várias vezes durante o programa para render todos os
							// elementos e lógicas presentes no jogo
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		batch.setProjectionMatrix(camera.combined);

		batch.begin(); // Início do desenho dos elementos
		font.setColor(Color.YELLOW); // Define a cor do texto

		carregaInitialElements();

		contagemTempoFase1(); // CHAMADA DAS FUNÇÕES

		acabouOJogoOuPassouDeFase();

		sapoGameWin = (Frogg) sapo; // instancia um objeto da Classe Frogg

		verificaVenceuOuPerdeu();

		batch.end(); // Fim do desenho dos elementos

		sapo.handleEvent(camera); // Chama a função que trata os eventos do jogador com o sapo

		spawnVehiclesPeloTempo();

		Iterator<Movel> iter = veiculos.iterator();
		while (iter.hasNext()) {
			Movel vehicle = iter.next();
			vehicle.move();
			if (vehicle.getmRectangle().x > 800 + vehicle.getmRectangle().width) { // Verifica se o veículo saiu da tela
																					// e remove-o da lista
				iter.remove();
			}
			if (vehicle.getmRectangle().overlaps(sapo.getmRectangle())) { // Verifica se o sapo colidiu com o veículo e
																			// volta com ele para a posição inicial
				((Frogg) sapo).setPosition(800 / 2 - 64 / 2, 10);
				if (((Frogg) sapo).lives > 0 && (gameWin2 == false && !gameWin1) || game2 == true) { // Sapo perde uma
																										// vida
					((Frogg) sapo).lives--;
					if (fase == 2) {
						gameOver2 = true;
					}
				}
			}
			if (sapo instanceof Frogg) { // Verifica se o objeto sapo da classe Movel foi instaciado a partir de métodos
											// da Classe Frogg
				if (((Frogg) sapo).isOnFinishLine(sapo, initScreen, frogg, gameOver)) { // Verifica se o sapo está na
																						// linha de chegada
					if (fase == 1)
						gameWin1 = true;
					else if (fase == 2 && gameWin1 == false) {
						gameWin2 = true;
					}
				}
			}
		}

	}

	@Override
	public void dispose() { // Função que é chamada quando o programa é fechado

		frogg.dispose();
		road.dispose();
		Wall.dispose();
		Finish.dispose();
		health.dispose();
		truckBombeiro.dispose();
		truck.dispose();
		carYellow.dispose();
		carGreen.dispose();
		carBlue.dispose();
		backgroundMusic.dispose();
		gameOverSound.dispose();
		victorySound.dispose();
		font.dispose();
		batch.dispose();
	}

}

abstract class Movel { // Classe abstrata que será herdada pelas classes que representarão os veículos
						// e o sapo

	protected Texture mImage;
	protected Rectangle mRectangle;
	protected Sound mSound;

	public Movel(Rectangle rectangle, Texture texture, Sound sound) {
		mRectangle = rectangle;
		mImage = texture;
		mSound = sound;
	}

	public abstract void move();

	public abstract void handleEvent(OrthographicCamera camera);

	public Texture getmImage() {
		return mImage;
	}

	public void setmImage(Texture mImage) {
		this.mImage = mImage;
	}

	public Rectangle getmRectangle() {
		return mRectangle;
	}

	public void setmRectangle(Rectangle mRectangle) { // GETTERS AND SETTERS
		this.mRectangle = mRectangle;
	}

	public Sound getmSound() {
		return mSound;
	}

	public void setmSound(Sound mSound) {
		this.mSound = mSound;
	}
}

class InitScreen { // Classe que representará elementos do início do jogo

	protected Texture mImage;
	protected Rectangle mRectangle;
	protected Sound mSound;

	public InitScreen(Rectangle rectangle, Texture texture, Sound sound) {
		mRectangle = rectangle;
		mImage = texture;
		mSound = sound;
	}

	public Texture getmImage() {
		return mImage;
	}

	public void setmImage(Texture mImage) { // GETTERS AND SETTERS
		this.mImage = mImage;
	}

	public Rectangle getmRectangle() {
		return mRectangle;
	}

	public void setmRectangle(Rectangle mRectangle) {
		this.mRectangle = mRectangle;
	}

}

class Road extends InitScreen { // Classe que representará a estrada do jogo

	public Road(Rectangle rectangle, Texture texture, Sound sound) {
		super(rectangle, texture, sound);
	}

}

class Frogg extends Movel { // Classe que representará o sapo do jogo

	public int lives = 3;
	private boolean gameover = false; // Variáveis utilizadas para controlar a vida, fim do jogo e gameOver
	private boolean gameWin2 = false;

	public Frogg(Rectangle rectangle, Texture texture, Sound sound) {
		super(rectangle, texture, sound);
	}

	private void movimentoSapo() { // Método que controla os movimentos do jogador com o sapo. (Jogador poderá
									// movimentar tanto com o WASD ou pelas setas do teclado)
		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
			mRectangle.x -= 150 * Gdx.graphics.getDeltaTime();
			setTexture(new Texture(Gdx.files.internal("assets/sapoEsquerda.png")));
		} else if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
			mRectangle.x += 150 * Gdx.graphics.getDeltaTime();
			setTexture(new Texture(Gdx.files.internal("assets/sapoDireita.png")));
		} else if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) {
			mRectangle.y -= 150 * Gdx.graphics.getDeltaTime();
			setTexture(new Texture(Gdx.files.internal("assets/sapoTras.png")));
		} else if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) {
			mRectangle.y += 150 * Gdx.graphics.getDeltaTime();
			setTexture(new Texture(Gdx.files.internal("assets/sapo.png")));
		}
	}

	private void limiteBordas() { // Método que não deixa o sapo sair da tela
		if (mRectangle.x < 0)
			mRectangle.x = 0;
		if (mRectangle.x > 800 - 64)
			mRectangle.x = 800 - 64;
		if (mRectangle.y > 310)
			mRectangle.y = 310;
		if (mRectangle.y < 0)
			mRectangle.y = 0;
	}

	@Override
	public void handleEvent(OrthographicCamera camera) { // Método que controla os eventos do jogador com o sapo e chama
															// o método de controle do sapo e de limite de bordas
		if (!(getgameOver()) && !gameWin2) {
			movimentoSapo();
		}
		limiteBordas();
	}

	public void setgameOver(boolean gameOver) {
		this.gameover = gameOver;
	}

	private boolean getgameOver() {
		return gameover;
	}

	public void setgameWin2(boolean gameWin2) { // GETTERS AND SETTERS
		this.gameWin2 = gameWin2;
	}

	private void setTexture(Texture texture) {
		setmImage(texture);

	}

	public void setPosition(float x, float y) {
		mRectangle.x = x;
		mRectangle.y = y;
	}

	public boolean isOnFinishLine(Movel sapo, InitScreen initScreen, Texture frogg, boolean gameOver) { // Método que
																										// verifica se o
																										// sapo está na
																										// linha de
																										// chegada ou
																										// não
		if (sapo.getmRectangle().y > 55 + initScreen.getmRectangle().height + frogg.getHeight() && gameOver == false) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void move() {

	}

}

class Veiculos extends Movel { // Classe que representará os veículos do jogo

	public Veiculos(Rectangle rectangle, Texture texture, Sound sound) {
		super(rectangle, texture, sound);
	}

	@Override
	public void move() {
		getmRectangle().x += 200 * Gdx.graphics.getDeltaTime(); // Movimentação dos veículos
	}

	@Override
	public void handleEvent(OrthographicCamera camera) {

	}
}
