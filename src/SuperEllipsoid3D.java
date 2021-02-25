import com.sun.swing.internal.plaf.metal.resources.metal_zh_HK;

//****************************************************************************
//     SuperEllipsoid class
//****************************************************************************
//PA4
//Xiaoxin GAN
//U90812154


public class SuperEllipsoid3D extends Objects3D
{	
	float r_x;
	float r_y;
	float r_z;
	float e1;
	float e2;
	public SuperEllipsoid3D(int id, float _x, float _y, float _z, float _r_x, float _r_y, float _r_z, float e1, float e2, int _m, int _n, ColorType KS, ColorType KD)
	{
		center = new Point3D(_x,_y,_z);
		r_x = _r_x;
		r_y = _r_y;
		r_z = _r_z;
		this.e1 = e1;
		this.e2 = e2;
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
		float d_phi=(float)Math.PI / ((float)(n-1));
		float c_theta,s_theta;
		float c_phi, s_phi;
		
		for(i=0,theta=-(float)Math.PI;i<m;++i,theta += d_theta)
	    {
			if (Math.abs(theta - (float)Math.PI)<0.00001)
				theta = (float)Math.PI;
			c_theta=(float)Math.cos(theta);
			s_theta=(float)Math.sin(theta);
						
			for(j=0,phi=-(float)(0.5*Math.PI);j<n;++j,phi += d_phi)
			{

				c_phi = (float)Math.cos(phi);
				s_phi = (float)Math.sin(phi);
				// handle with the error when compute
				 if (c_phi < 0)
					 c_phi = -c_phi;

				mesh.v[i][j].x = (float) (center.x + r_x * c_phi * Math.pow(Math.abs(c_phi), e1-1) * c_theta
						* Math.pow(Math.abs(c_theta), e2-1));
				mesh.v[i][j].y = (float) (center.y + r_y * c_phi * Math.pow(Math.abs(c_phi), e1-1) * s_theta
						* Math.pow(Math.abs(s_theta), e2-1));
				mesh.v[i][j].z = (float) (center.z + r_z * s_phi * Math.pow(Math.abs(s_phi), e1-1));			
				
				
				// unit normal to sphere at this vertex
				mesh.n[i][j].x = (float) (r_y*r_z*
						Math.pow(Math.abs(s_phi), e1-1)*c_theta*Math.pow(Math.abs(s_theta), e2-1));
				mesh.n[i][j].y = (float) (r_x*r_z*
						Math.pow(Math.abs(s_phi), e1-1)*s_theta*Math.pow(Math.abs(c_theta), e2-1));
				mesh.n[i][j].z = (float) (r_x*r_y*s_phi*Math.pow(Math.abs(c_phi), e1-2)*
						Math.pow(Math.abs(c_theta), e2-1)*Math.pow(Math.abs(s_theta), e2-1));
				
				mesh.n[i][j].normalize();
					
			}
	    }
	}
}