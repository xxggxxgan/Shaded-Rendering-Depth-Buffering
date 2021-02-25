//PA4
//Xiaoxin GAN
//U90812154

import java.awt.image.BufferedImage;

import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Position.Bias;

public class TexBump extends Objects3D
{	
	Point2D[][] p;
	int[][] bv;
	int[][] bu;
	private BufferedImage texture;
	boolean Bumpmap = false;
	
	public TexBump(int id, float _x, float _y, float _z, float _r, int _m, int _n, BufferedImage texture)
	{
		center = new Point3D(_x,_y,_z);
		r = _r;
		m = _m;
		n = _n;
		ID = id;
		this.texture = texture;
		
		setBumpTable(texture);

		initMesh();
	
	}
	
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	public void fillMesh() {
		int i, j;
		float theta, phi;
		float d_theta = (float) (2.0 * Math.PI) / ((float) (m - 1));
		float d_phi = (float) Math.PI / ((float) n - 1);
		float c_theta, s_theta;
		float c_phi, s_phi;
		

		

			for(i=0,theta=-(float)Math.PI;i<m;++i,theta += d_theta)
		    {
				c_theta=(float)Math.cos(theta);
				s_theta=(float)Math.sin(theta);

				for (j = 0, phi = (float) (-0.5 * Math.PI); j < n; ++j, phi += d_phi) {
					// vertex location
					c_phi = (float) Math.cos(phi);
					s_phi = (float) Math.sin(phi);

					mesh.v[i][j].x = center.x + r * c_phi * c_theta;
					mesh.v[i][j].y = center.y + r * c_phi * s_theta;
					mesh.v[i][j].z = center.z + r * s_phi;
					
					Point3D du = new Point3D();
					Point3D dv = new Point3D();
					Point3D normal = new Point3D();
					Point3D dun = new Point3D();
					Point3D dvn = new Point3D();
					
					int u,v; 
					u = (int) ((theta+Math.PI)/(2*Math.PI)*(texture.getWidth() - 1));
					v = (int) ((phi+Math.PI/2f)/Math.PI*(texture.getHeight() - 1));

					
					du.x = -r*s_theta;
					du.y = r*c_theta;
					du.z = 0;
					
					dv.x = -r * s_phi * c_theta;
					dv.y = -r * s_phi * s_theta;
					dv.z = r*c_phi;
					
					du.crossProduct(dv, normal);
					
					// unit normal to sphere at this vertex
					mesh.n[i][j].x = c_phi * c_theta;
					mesh.n[i][j].y = c_phi * s_theta;
					mesh.n[i][j].z = s_phi;
								
					du.crossProduct( mesh.n[i][j],dun);
					mesh.n[i][j].crossProduct(dv, dvn);
					
					dun = dun.scale(bv[v][u]);
					dvn = dvn.scale(bu[v][u]);
					
					mesh.n[i][j] = normal.plus(dun.plus(dvn));
					mesh.n[i][j].normalize();
	
				}
			
		}
	}

	public void setBumpTable(BufferedImage texture){
		int width, height;
		width = texture.getWidth();
		height = texture.getHeight();
		bv = new int[height][width];
		bu = new int[height][width];
		for (int i = 0; i < height; i++){
			for (int j = 0; j < width; j++)
			{
				int b, u1, u2, v1, v2;
				b = texture.getRGB(j, i);
				u1 = b&0x000000ff;
				v1 = u1;
				b = texture.getRGB((j+1) % width, i);
				u2 = b&0x000000ff;
				bu[i][j] = u2 - u1;
				b = texture.getRGB(j, (height + i - 1) % height);
				v2 = b&0x000000ff;
				bv[i][j] = v2 - v1;
			}	
		}
		Bumpmap = true;
		ks = new ColorType();
		kd = new ColorType();
	}
}