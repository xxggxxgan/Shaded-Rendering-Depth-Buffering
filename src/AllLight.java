// this class is define as a the light of all scene
// So in every scene we have infinite light, attenuation, point light, and ambient light
//Xiaoxin GAN
//U90812154
//Xiaoxin GAN
//U90812154
import java.awt.Point;
import java.util.Set;

public class AllLight {
	private Point3D InfiniteLightDirection;
	private Point3D AtteLightDirection;
	private ColorType AmbientLightColor;
	private ColorType InfiniteLightColor;
	private ColorType PointLightColor;
	private Point3D PointLightPosition;
	private float AtteLightScope;
	private float RadAtten, AngAtten;
	private float a0 = 1f, a1 = 0.00001f, a2 = 0.00000009f;// for spot light
	private float PowCspotLight = 5000;//for angular atten
	private boolean AmbientLight = false;
	private boolean InfiniteLight = false;
	private boolean PointLight = false;
	private boolean AttenLight = false;

	
	public Point3D position;
	public Point3D direction;
	public Point3D max_intensity_vl;
	public ColorType color;
	public boolean radial,angular;

	public float theta_l;
	public float al;
		
	public AllLight(boolean AmbientLight, ColorType AmbientLightColor,
			boolean InfiniteLight, Point3D InfiniteLightDirection, ColorType InfiniteLightColor,
			boolean AttenLight, boolean DirAttenLight, Point3D AtteLightDirection, ColorType AttenLightColor, Point3D AttenLightPosition, 
			float scope,boolean _radial, boolean _angular){
		
		this.AmbientLightColor = new ColorType(AmbientLightColor);
		this.AmbientLight = AmbientLight;
		
		this.InfiniteLightDirection = new Point3D(InfiniteLightDirection);
		this.InfiniteLightDirection.normalize();
		this.InfiniteLightColor = new ColorType(InfiniteLightColor);
		this.InfiniteLight = InfiniteLight;
		
		this.AtteLightDirection = new Point3D(AtteLightDirection);
		this.AtteLightDirection.normalize();
		this.PointLightColor = new ColorType(AttenLightColor);
		this.PointLightPosition = new Point3D(AttenLightPosition);
		this.PointLight = AttenLight;
		AtteLightScope = scope;
		this.AttenLight = DirAttenLight;
		
		radial = _radial;
		angular = _angular;
		
	}
	
	
	// apply this light source to the vertex / normal, given material
	// return resulting color value
	public ColorType applyLight(Material mat, Point3D v, Point3D n, Point2D location) {
		ColorType res = new ColorType();

		// dot product between light direction and normal
		// light must be facing in the positive direction
		// dot <= 0.0 implies this light is facing away (not toward) this point
		// therefore, light only contributes if dot > 0.0
		

		if (AmbientLight && mat.ambient) {
			res.r = (float) (mat.ka.r * AmbientLightColor.r);
			res.g = (float) (mat.ka.g * AmbientLightColor.g);
			res.b = (float) (mat.ka.b * AmbientLightColor.b);
		}

		if (InfiniteLight) {
			double dot = InfiniteLightDirection.dotProduct(n);
			if (dot > 0.0) {
				// diffuse component
				if (mat.diffuse) {
					res.r += (float) (dot * mat.kd.r * InfiniteLightColor.r);
					res.g += (float) (dot * mat.kd.g * InfiniteLightColor.g);
					res.b += (float) (dot * mat.kd.b * InfiniteLightColor.b);
				}
				// specular component
				if (mat.specular) {
					Point3D r = InfiniteLightDirection.reflect(n);
					dot = r.dotProduct(v);
					if (dot > 0.0) {
						res.r += (float) Math.pow((dot * mat.ks.r * InfiniteLightColor.r), mat.ns);
						res.g += (float) Math.pow((dot * mat.ks.g * InfiniteLightColor.g), mat.ns);
						res.b += (float) Math.pow((dot * mat.ks.b * InfiniteLightColor.b), mat.ns);
					}
				}

				// clamp so that allowable maximum illumination level is not exceeded
			}
		}

		if (PointLight || AttenLight) {
			
			float Distance;
			Point3D v1 = new Point3D();
			float dpro;
			v1.x = PointLightPosition.x -location.x;
			v1.y = PointLightPosition.y -location.y;
			v1.z = PointLightPosition.z -location.z;
			v1.normalize();
			double dot = v1.dotProduct(n);

			Distance = distance(location);

			// compute the ridial attenuation
			RadAtten = (float) (1 / (a0 + a1 * Distance + a2 * Math.pow(Distance, 2)));

			// compute the angle attenuation
			if (AttenLight) {
				dpro = AtteLightDirection.dotProduct(v1);
				if (dpro < Math.cos(AtteLightScope))
					AngAtten = 0;
				else{
					AngAtten = (float) Math.pow(dpro, PowCspotLight);
				}
			} 
			else
				AngAtten = 1;

			if (dot > 0.0 && AngAtten > 0) {
				// diffuse component
				if (mat.diffuse) {
					res.r += (float) (dot * mat.kd.r * PointLightColor.r) * RadAtten * AngAtten;
					res.g += (float) (dot * mat.kd.g * PointLightColor.g) * RadAtten * AngAtten;
					res.b += (float) (dot * mat.kd.b * PointLightColor.b) * RadAtten * AngAtten;
				}
				// specular component
				if (mat.specular) {
					Point3D r = v1.reflect(n);
					dot = r.dotProduct(v);
					if (dot > 0.0) {
						res.r += (float) Math.pow((dot * mat.ks.r * PointLightColor.r), mat.ns) * RadAtten * AngAtten;
						res.g += (float) Math.pow((dot * mat.ks.g * PointLightColor.g), mat.ns) * RadAtten * AngAtten;
						res.b += (float) Math.pow((dot * mat.ks.b * PointLightColor.b), mat.ns) * RadAtten * AngAtten;
					}
				}

			}
			if(radial)
			{	
				RadAtten = (float) (1 / (a0 + a1 * Distance + a2 * Math.pow(Distance, 2)));
				res.r *= RadAtten;
				res.g *= RadAtten;
				res.b *= RadAtten;
			}
			if(angular)
			{
				float cos_alpha;
				cos_alpha = direction.dotProduct(max_intensity_vl);
				//check if the point in the cone
				if (cos_alpha < Math.cos(theta_l)) {
					res.r *= Math.pow(cos_alpha, al);
					res.g *= Math.pow(cos_alpha, al);
					res.b *= Math.pow(cos_alpha, al);
				}
			}

		}
		// clamp so that allowable maximum illumination level is not
		// exceeded
		res.r = (float) Math.min(1.0, res.r);
		res.g = (float) Math.min(1.0, res.g);
		res.b = (float) Math.min(1.0, res.b);

		return (res);
	}


	public float distance(Point2D location){
		float D;
		float dx, dy, dz;
		dx = PointLightPosition.x - location.x;
		dy = PointLightPosition.y - location.y;
		dz = PointLightPosition.z - location.z;
		
		D = (float) Math.pow(dx*dx+dy*dy+dz*dz, 0.5);
		
		return D;
	}
	
	public void seta0(float _a0){
		a0 = _a0;
	}
	
	public void seta1(float _a1){
		a1 = _a1;
	}
	
	public void seta2(float _a2){
		a2 = _a2;
	}
	
	public void intensityAlpha(float alpha){
		PowCspotLight = alpha;
	}

	// for rotate the light
	public void rotateLight(Quaternion q, Point3D center) {
		Quaternion q_inv = q.conjugate();
		Point3D vec;

		Quaternion p;

		// apply pivot rotation to vertices, given center point
		p = new Quaternion((float) 0.0, PointLightPosition.minus(center));
		p = q.multiply(p);
		p = p.multiply(q_inv);
		vec = p.get_v();
		PointLightPosition = vec.plus(center);

		// rotate the normals
		p = new Quaternion((float) 0.0, InfiniteLightDirection);
		p = q.multiply(p);
		p = p.multiply(q_inv);
		InfiniteLightDirection.x = p.get_v().x;
		InfiniteLightDirection.y = p.get_v().y;
		InfiniteLightDirection.z = p.get_v().z;
		
		p = new Quaternion((float) 0.0, AtteLightDirection);
		p = q.multiply(p);
		p = p.multiply(q_inv);
		AtteLightDirection.x = p.get_v().x;
		AtteLightDirection.y = p.get_v().y;
		AtteLightDirection.z = p.get_v().z;

	}
	
}