//PA4
//Xiaoxin GAN
//U90812154

import java.awt.image.BufferedImage;

import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Position.Bias;

public class TexEn extends Objects3D
{	
	Point2D[][] p;
	int[][] bv;
	int[][] bu;
	private BufferedImage texture;

	public TexEn(int id, float _x, float _y, float _z, float _r, int _m, int _n, BufferedImage texture)
	{
		center = new Point3D(_x,_y,_z);
		r = _r;
		m = _m;
		n = _n;
		ID = id;
		this.texture = texture;
		

			p = new Point2D[m][n];
			for (int i = 0; i < m; i++)
				for (int j = 0; j < n; j++)
					p[i][j] = new Point2D();




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
		
	
			for (i = 0, theta = 0 * (float) Math.PI; i < m; ++i, theta += d_theta) {
				c_theta = (float) Math.cos(theta);
				s_theta = (float) Math.sin(theta);

				for (j = 0, phi = (float) (0.5 * Math.PI); j < n; ++j, phi -= d_phi) {
					// vertex location
					c_phi = (float) Math.cos(phi);
					s_phi = (float) Math.sin(phi);
					mesh.v[i][j].x = center.x + r * c_phi * c_theta;
					mesh.v[i][j].z = center.z + r * c_phi * s_theta;
					mesh.v[i][j].y = center.y + r * s_phi;

					p[i][j].x = (int) (center.x + r * c_phi * c_theta);
					p[i][j].z = (int) (center.z + r * c_phi * s_theta);
					p[i][j].y = (int) (center.y + r * s_phi);
					p[i][j].u = (float) (theta / (2 * Math.PI)) * (texture.getWidth() - 1);
					p[i][j].v = (float) ((phi + 0.5 * Math.PI) / Math.PI) * (texture.getHeight() - 1);
				}
			}
		
		
		
			
		}
	}

