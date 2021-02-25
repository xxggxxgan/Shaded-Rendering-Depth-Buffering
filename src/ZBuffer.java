//Xiaoxin GAN
//U90812154
public class ZBuffer{
	private int[][] buffer;
	private int InfiniteZ = -5000;
	
	public void InitialZBuffer(){
		buffer = new int [800][800];
	}
	
	public void CleanZBuffer(){
		for (int i = 0; i < 800; i++)
			for (int j = 0; j < 800; j++)
				buffer[i][j] = InfiniteZ;
	}
	
	public int GetZ(int x, int y){
		return buffer[x][y];
	}
	
	public void SetZ(int x, int y, int z){
		buffer[x][y] = z;
	}
}