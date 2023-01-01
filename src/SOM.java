import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;


public class SOM {
	Vec2D [][] neurony;
	double eta, epsEta;
	double S, epsS;
	int w, h;

	public SOM(){
		neurony=null;
		eta=0.0;
	}
	public SOM(int w,int h,double aeta,double aepsEta,double aepsS){
		this.w = w; this.h = h;
		eta=aeta; epsEta=aepsEta;
		S=Math.sqrt(w*h);
		epsS=aepsS;
		neurony=new Vec2D[h][];
		for(int i=0;i<h;i++)
			neurony[i]=new Vec2D[w];
		Random r=new Random();
		for(int i=0;i<neurony.length;i++)
			for(int j=0;j<neurony[i].length;j++){
				double a=0.01*(r.nextDouble()-0.5)/0.5;
				double b=0.01*(r.nextDouble()-0.5)/0.5;
				neurony[i][j]=new Vec2D(a,b);
			}
	}
	public void draw(Graphics g,int x0,int y0,int w,int h){
		int x = 0, y = 0;
		int beginX = 0, beginY = 0;

		for(int i=0;i<neurony.length;i++) {
			for(int j=0;j<neurony[i].length;j++){
				x=w2x(x0,w,neurony[i][j]);
				y=w2y(y0,h,neurony[i][j]);
				if((i == 0) && (j == 0)) {
					beginX = x;
					beginY = y;
				}
				int x1,y1;
				g.setColor(Color.BLUE);
				if(i+1<neurony.length){
					x1=w2x(x0,w,neurony[i+1][j]);
					y1=w2y(y0,h,neurony[i+1][j]);
					g.drawLine(x, y, x1, y1);
				}
				if(j+1<neurony[i].length){
					x1=w2x(x0,w,neurony[i][j+1]);
					y1=w2y(y0,h,neurony[i][j+1]);
					g.drawLine(x, y, x1, y1);
				}
				g.setColor(Color.RED);
				g.fillOval(x-3, y-3, 6, 6);
			}
		}

		if(this.w == 1) {
			g.setColor(Color.BLUE);
			g.drawLine(x, y, beginX, beginY);
		}
	}
	public int w2x(int a,int b,Vec2D v){
		return a+(int)Math.round(b*(v.x+1.0)/2.0);
	}
	public int w2y(int a,int b,Vec2D v){
		return a+(int)Math.round(b*(v.y+1.0)/2.0);
	}
	public void learn(Vec2D input){
		int [] winner = findWinner(input);
		if(w == 1)
			correctStringWeights(winner, input);
		else
			correctWeights(winner, input);
		eta*=epsEta;
		S*=epsS;
	}

	protected void correctStringWeights(int [] winner, Vec2D input) {
		int idxW = winner[0];
		double d=0.0;
		int SS=(int)S;
		int idX;

		for(int i=idxW-SS;i<=idxW+SS;i++) {
			d = Math.abs(idxW - i);
			idX = i < 0 ? neurony.length + i : i % neurony.length;
			neurony[idX][0].add(Vec2D.sub(input, neurony[idX][0]).mul(eta).mul(fS(d)));
		}
	}

	protected void correctWeights(int [] winner, Vec2D input) {
		int idxW = winner[0];
		int idxK = winner[1];
		double d=0.0;
		int SS=(int)S;

		for(int i=idxW-SS;i<=idxW+SS;i++) {
			if(i>=0 && i<neurony.length) {
				for(int j=idxK-SS;j<=idxK+SS;j++) {
					if(j>=0 && j<neurony[i].length){
						d=Math.sqrt(Math.pow(idxW-i, 2.0)+Math.pow(idxK-j, 2.0));
						if(d<S) {
							neurony[i][j].add(Vec2D.sub(input, neurony[i][j]).mul(eta).mul(fS(d)));
						}
					}
				}
			}
		}
		//neurony[idxW][idxK].add(Vec2D.sub(wejscia,neurony[idxW][idxK]).mul(eta).mul(fS(d)));
	}

	protected int[] findWinner(Vec2D input) {
		int [] result = new int[2];
		int idxW=0,idxK=0;
		double minDist=Vec2D.distance(neurony[idxW][idxK],input);
		for(int i=0;i<neurony.length;i++) {
			for(int j=0;j<neurony[i].length;j++){
				double dist=Vec2D.distance(neurony[i][j],input);
				if(dist<minDist){
					idxW=i;
					idxK=j;
					minDist=dist;
				}
			}
		}
		result[0] = idxW;
		result[1] = idxK;
		return result;
	}

	public void config(double aeta,double aepsEta,double aepsS) {
		eta=aeta; epsEta=aepsEta;
		S=Math.sqrt(w*h);
		epsS=aepsS;
	}

	public double getEta() {
		return eta;
	}

	public double fS(double d){
		return 1.0-d/S;
	}
}
