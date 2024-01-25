
public class Vec2D {
	double x,y;
	public Vec2D(){
		x=y=0;
	}
	public Vec2D(double ax,double ay){
		x=ax; y=ay;
	}
	public static Vec2D add(Vec2D a,Vec2D b){
		return new Vec2D(a.x+b.x,a.y+b.y);
	}
	public Vec2D add(Vec2D a){
		x+=a.x; y+=a.y;
		return this;
	}
	public static Vec2D sub(Vec2D a,Vec2D b){
		return new Vec2D(a.x-b.x,a.y-b.y);
	}
	public Vec2D sub(Vec2D a){
		x-=a.x; y-=a.y;
		return this;
	}
	public Vec2D mul(double a){
		x*=a; y*=a;
		return this;
	}
}
