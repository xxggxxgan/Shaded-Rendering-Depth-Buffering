//****************************************************************************
//      Sphere class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff

public class Ellipse3D extends Objects3D
{	
	float rx;
	float ry;
	float rz;
	public Ellipse3D(int id, float _x, float _y, float _z, float _rx, float _ry, float _rz,int _m, int _n, ColorType KS, ColorType KD)
	{
		center = new Point3D(_x,_y,_z);
		rx = _rx;
		ry = _ry;
		rz = _rz;
		m = _m;
		n = _n;
		ks = new ColorType(KS);
		kd = new ColorType(KD);
		ID = id;
		initMesh();
	}
		
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	public void fillMesh()
	{
		int i,j;		
		float theta, phi;
		float d_theta=(float)(2.0*Math.PI)/ ((float)(m-1));
		float d_phi=(float)Math.PI / ((float)n-1);
		float c_theta,s_theta;
		float c_phi, s_phi;
		
		for(i=0,theta=-(float)Math.PI;i<m;++i,theta += d_theta)
	    {
			c_theta=(float)Math.cos(theta);
			s_theta=(float)Math.sin(theta);
			
			for(j=0,phi=(float)(-0.5*Math.PI);j<n;++j,phi += d_phi)
			{
				// vertex location
				c_phi = (float)Math.cos(phi);
				s_phi = (float)Math.sin(phi);
				mesh.v[i][j].x=center.x+rx*c_phi*c_theta;
				mesh.v[i][j].y=center.y+ry*c_phi*s_theta;
				mesh.v[i][j].z=center.z+rz*s_phi;
				
				// unit normal to sphere at this vertex
				mesh.n[i][j].x = ry*rz*c_phi*c_theta;
				mesh.n[i][j].y = rx*rz*c_phi*s_theta;
				mesh.n[i][j].z = rx*ry*s_phi;
				mesh.n[i][j].normalize();
			}
	    }
	}
}