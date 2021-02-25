//****************************************************************************
//      Sphere class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff


public class Cylinder3D extends Objects3D
{	
	float rx;
	float ry;
	float length;
	float rz;
	public Cylinder3D(int id, float _x, float _y, float _z, float _rx, float _ry, float _length,int _m, int _n, ColorType KS, ColorType KD)
	{
		center = new Point3D(_x,_y,_z);
		rx = _rx;
		ry = _ry;
		rz = Math.min(rx, ry);
		length = _length;
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
		int Sn = n/3;
		int Cn = n-2*Sn;
		float theta, phi,k;
		float d_k=(float)(length/Cn);
		float d_theta=(float)(2.0*Math.PI)/ ((float)(m-1));
		float d_phi=(float)(0.5*Math.PI) / ((float)Sn-1);
		float c_theta,s_theta;
		float c_phi, s_phi;
		
		for(i=0,theta=-(float)Math.PI;i<m;++i,theta += d_theta)
	    {
			c_theta=(float)Math.cos(theta);
			s_theta=(float)Math.sin(theta);
			
			for(j=Sn-1,k=(float)(-length/2),phi=(float)(0*Math.PI);j>=0;j--, phi += d_phi)
			{
				// vertex location
				c_phi = (float)Math.cos(phi);
		 		s_phi = (float)Math.sin(phi);
				mesh.v[i][j].x=center.x+rx*c_phi*c_theta;
				mesh.v[i][j].y=center.y+ry*c_phi*s_theta;
				mesh.v[i][j].z=center.z-length/2+rz*(-s_phi);
				
				
				// unit normal to sphere at this vertex
				mesh.n[i][j].x = ry*rz*c_phi*c_theta;
				mesh.n[i][j].y = rx*rz*c_phi*s_theta;
				mesh.n[i][j].z = rx*ry*(-s_phi);
				mesh.n[i][j].normalize();
					
				mesh.v[i][n-1-j].x=center.x+rx*c_phi*c_theta;
				mesh.v[i][n-1-j].y=center.y+ry*c_phi*s_theta;
				mesh.v[i][n-1-j].z=center.z+length/2+rz*s_phi;
				
				// unit normal to sphere at this vertex
				mesh.n[i][n-1-j].x = ry*rz*c_phi*c_theta;
				mesh.n[i][n-1-j].y = rx*rz*c_phi*s_theta;
				mesh.n[i][n-1-j].z = rx*ry*s_phi;
				mesh.n[i][n-1-j].normalize();
				
				if (k < (float) (length / 2)) {
					mesh.v[i][2*Sn - j - 1].x = center.x + rx * c_theta;
					mesh.v[i][2*Sn - j - 1].y = center.y + ry * s_theta;
					mesh.v[i][2*Sn - j - 1].z = center.z + k;

					// unit normal to sphere at this vertex
					mesh.n[i][2*Sn - j - 1].x = ry * c_theta;
					mesh.n[i][2*Sn - j - 1].y = rx * s_theta;
					mesh.n[i][2*Sn - j - 1].z = 0;
					mesh.n[i][2*Sn - j - 1].normalize();
					k += d_k;
				}
			}
	    }
	}
}