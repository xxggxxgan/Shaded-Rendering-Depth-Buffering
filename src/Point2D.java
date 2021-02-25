import java.awt.Point;

//****************************************************************************
//       2D Point Class from PA1
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff


public class Point2D
{
	public int x, y, z;
	public float u, v; // uv coordinates for texture mapping
	public ColorType c;
	public Point2D(int _x, int _y, int _z, ColorType _c)
	{
		u = 0;
		v = 0;
		x = _x;
		y = _y;
		z = _z;
		c = _c;
	}
	public Point2D(int _x, int _y, int _z, ColorType _c, float _u, float _v)
	{
		u = _u;
		v = _v;
		x = _x;
		y = _y;
		z = _z;
		c = _c;
	}
	
	public Point2D(int _x, int _y, int _z){
		x = _x;
		y = _y;
		z = _z;
		u = 0;
		v = 0;
		c = new ColorType();
	}
	
	public Point2D minus(Point2D v)
	{
		Point2D out = new Point2D();
		out.x = this.x-v.x;
		out.y = this.y-v.y;
		out.z = this.z-v.z;
		return(out);
	}
	
	public Point2D()
	{
		c = new ColorType(1.0f, 1.0f, 1.0f);
	}
	public Point2D( Point2D p)
	{
		u = p.u;
		v = p.v;
		x = p.x;
		y = p.y;
		z = p.z;
		c = new ColorType(p.c.r, p.c.g, p.c.b);
	}
}