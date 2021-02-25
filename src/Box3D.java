//PA4
//Xiaoxin GAN
//U90812154

public class Box3D extends Objects3D{
	float length;
	public Box3D(int id, float _x, float _y, float _z, float _length,ColorType KS, ColorType KD){
		ID = id;
		center = new Point3D(_x,_y,_z);
		length = _length;
		ks = new ColorType(KS);
		kd = new ColorType(KD);
		m = 6;
		n = 4;
		initMesh();
	}
	
	protected void fillMesh()
	{
		// 6 plane and for each plane we have 4 points.
		mesh.v[0][0].x = -length/2 + center.x;
		mesh.v[0][0].y = length/2 + center.y;
		mesh.v[0][0].z = length/2 + center.z;
		mesh.n[0][0].x = 0;
		mesh.n[0][0].y = 0;
		mesh.n[0][0].z = 1;
		
		mesh.v[0][1].x = -length/2 + center.x;
		mesh.v[0][1].y = -length/2 + center.y;
		mesh.v[0][1].z = length/2 + center.z;
		mesh.n[0][1].x = 0;
		mesh.n[0][1].y = 0;
		mesh.n[0][1].z = 1;
		
		mesh.v[0][2].x = length/2 + center.x;
		mesh.v[0][2].y = -length/2 + center.y;
		mesh.v[0][2].z = length/2 + center.z;
		mesh.n[0][2].x = 0;
		mesh.n[0][2].y = 0;
		mesh.n[0][2].z = 1;
		
		mesh.v[0][3].x = length/2 + center.x;
		mesh.v[0][3].y = length/2 + center.y;
		mesh.v[0][3].z = length/2 + center.z;
		mesh.n[0][3].x = 0;
		mesh.n[0][3].y = 0;
		mesh.n[0][3].z = 1;
		
		mesh.v[1][0].x = -length/2 + center.x;
		mesh.v[1][0].y = -length/2 + center.y;
		mesh.v[1][0].z = length/2 + center.z;
		mesh.n[1][0].x = 0;
		mesh.n[1][0].y = -1;
		mesh.n[1][0].z = 0;
		
		mesh.v[1][1].x = -length/2 + center.x;
		mesh.v[1][1].y = -length/2 + center.y;
		mesh.v[1][1].z = -length/2 + center.z;
		mesh.n[1][1].x = 0;
		mesh.n[1][1].y = -1;
		mesh.n[1][1].z = 0;
		
		mesh.v[1][2].x = length/2 + center.x;
		mesh.v[1][2].y = -length/2 + center.y;
		mesh.v[1][2].z = -length/2 + center.z;
		mesh.n[1][2].x = 0;
		mesh.n[1][2].y = -1;
		mesh.n[1][2].z = 0;
		
		mesh.v[1][3].x = length/2 + center.x;
		mesh.v[1][3].y = -length/2 + center.y;
		mesh.v[1][3].z = length/2 + center.z;
		mesh.n[1][3].x = 0;
		mesh.n[1][3].y = -1;
		mesh.n[1][3].z = 0;
		
		mesh.v[2][0].x = -length/2 + center.x;
		mesh.v[2][0].y = -length/2 + center.y;
		mesh.v[2][0].z = -length/2 + center.z;
		mesh.n[2][0].x = 0;
		mesh.n[2][0].y = 0;
		mesh.n[2][0].z = -1;
		
		mesh.v[2][1].x = -length/2 + center.x;
		mesh.v[2][1].y = length/2 + center.y;
		mesh.v[2][1].z = -length/2 + center.z;
		mesh.n[2][1].x = 0;
		mesh.n[2][1].y = 0;
		mesh.n[2][1].z = -1;
		
		mesh.v[2][2].x = length/2 + center.x;
		mesh.v[2][2].y = length/2 + center.y;
		mesh.v[2][2].z = -length/2 + center.z;
		mesh.n[2][2].x = 0;
		mesh.n[2][2].y = 0;
		mesh.n[2][2].z = -1;
		
		mesh.v[2][3].x = length/2 + center.x;
		mesh.v[2][3].y = -length/2 + center.y;
		mesh.v[2][3].z = -length/2 + center.z;
		mesh.n[2][3].x = 0;
		mesh.n[2][3].y = 0;
		mesh.n[2][3].z = -1;
		
		mesh.v[3][0].x = -length/2 + center.x;
		mesh.v[3][0].y = length/2 + center.y;
		mesh.v[3][0].z = -length/2 + center.z;
		mesh.n[3][0].x = 0;
		mesh.n[3][0].y = 1;
		mesh.n[3][0].z = 0;
		
		mesh.v[3][1].x = -length/2 + center.x;
		mesh.v[3][1].y = length/2 + center.y;
		mesh.v[3][1].z = length/2 + center.z;
		mesh.n[3][1].x = 0;
		mesh.n[3][1].y = 1;
		mesh.n[3][1].z = 0;
		
		mesh.v[3][2].x = length/2 + center.x;
		mesh.v[3][2].y = length/2 + center.y;
		mesh.v[3][2].z = length/2 + center.z;
		mesh.n[3][2].x = 0;
		mesh.n[3][2].y = 1;
		mesh.n[3][2].z = 0;
		
		mesh.v[3][3].x = length/2 + center.x;
		mesh.v[3][3].y = length/2 + center.y;
		mesh.v[3][3].z = -length/2 + center.z;
		mesh.n[3][3].x = 0;
		mesh.n[3][3].y = 1;
		mesh.n[3][3].z = 0;
		
		
		mesh.v[4][0].x = length/2 + center.x;
		mesh.v[4][0].y = length/2 + center.y;
		mesh.v[4][0].z = length/2 + center.z;
		mesh.n[4][0].x = 1;
		mesh.n[4][0].y = 0;
		mesh.n[4][0].z = 0;
		
		mesh.v[4][1].x = length/2 + center.x;
		mesh.v[4][1].y = -length/2 + center.y;
		mesh.v[4][1].z = length/2 + center.z;
		mesh.n[4][1].x = 1;
		mesh.n[4][1].y = 0;
		mesh.n[4][1].z = 0;
		
		mesh.v[4][2].x = length/2 + center.x;
		mesh.v[4][2].y = -length/2 + center.y;
		mesh.v[4][2].z = -length/2 + center.z;
		mesh.n[4][2].x = 1;
		mesh.n[4][2].y = 0;
		mesh.n[4][2].z = 0;
		
		mesh.v[4][3].x = length/2 + center.x;
		mesh.v[4][3].y = length/2 + center.y;
		mesh.v[4][3].z = -length/2 + center.z;
		mesh.n[4][3].x = 1;
		mesh.n[4][3].y = 0;
		mesh.n[4][3].z = 0;
		
		mesh.v[5][0].x = -length/2 + center.x;
		mesh.v[5][0].y = length/2 + center.y;
		mesh.v[5][0].z = -length/2 + center.z;
		mesh.n[5][0].x = -1;
		mesh.n[5][0].y = 0;
		mesh.n[5][0].z = 0;
		
		mesh.v[5][1].x = -length/2 + center.x;
		mesh.v[5][1].y = -length/2 + center.y;
		mesh.v[5][1].z = -length/2 + center.z;
		mesh.n[5][1].x = -1;
		mesh.n[5][1].y = 0;
		mesh.n[5][1].z = 0;
		
		mesh.v[5][2].x = -length/2 + center.x;
		mesh.v[5][2].y = -length/2 + center.y;
		mesh.v[5][2].z = length/2 + center.z;
		mesh.n[5][2].x = -1;
		mesh.n[5][2].y = 0;
		mesh.n[5][2].z = 0;
		
		mesh.v[5][3].x = -length/2 + center.x;
		mesh.v[5][3].y = length/2 + center.y;
		mesh.v[5][3].z = length/2 + center.z;
		mesh.n[5][3].x = -1;
		mesh.n[5][3].y = 0;
		mesh.n[5][3].z = 0;
		
	}
	
	
}