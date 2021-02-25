
//****************************************************************************
// SketchBase.  
//****************************************************************************
// Comments : 
//   Subroutines to manage and draw points, lines an triangles
//

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;

public class SketchBase {

	private Material mats;
	private Point3D view_vector;
	private AllLight light;
	// this is for phong rendering, this three normal are the normal of three
	// vertex sent to sketch base
	private Point3D normal1, normal2, normal3;
	private BufferedImage texture;

	public SketchBase() {
		normal1 = new Point3D();
		normal2 = new Point3D();
		normal3 = new Point3D();
	}

	/**********************************************************************
	 * Draws a point. This is achieved by changing the color of the buffer at
	 * the location corresponding to the point and compare with the point in
	 * Z-BUFF TO deside who should be draw.
	 * 
	 * @param buff
	 *             Buffer object.
	 * @param Zbuff
	 * 			   ZBuffer object.
	 * @param p
	 *            Point to be drawn.
	 */
	public static void drawPoint(BufferedImage buff, ZBuffer Zbuff, Point2D p) {
		if (p.x >= 0 && p.x < buff.getWidth() && p.y >= 0 && p.y < buff.getHeight())
			if (p.z > Zbuff.GetZ(p.x, p.y))
				buff.setRGB(p.x, buff.getHeight() - p.y - 1, p.c.getRGB_int());
	}

	/**********************************************************************
	 * Draws a line segment using Bresenham's algorithm, linearly interpolating
	 * RGB color along line segment. This method only uses integer arithmetic.
	 * 
	 * @param buff,
	 *            Buffer object.
     * @param Zbuff
	 * 			   ZBuffer object.
	 * @param p1
	 *            First given endpoint of the line.
	 * @param p2
	 *            Second given endpoint of the line.
	 */
	// we add the
	public void drawLine(BufferedImage buff, ZBuffer Zbuff, Point2D p1, Point2D p2) {
		int x0 = p1.x, y0 = p1.y;
		int xEnd = p2.x, yEnd = p2.y;
		int dx = Math.abs(xEnd - x0), dy = Math.abs(yEnd - y0);

		if (dx == 0 && dy == 0) {
			if (p1.z >= p2.z) {
				drawPoint(buff, Zbuff, p1);
			} else
				drawPoint(buff, Zbuff, p2);
			return;
		}

		// if slope is greater than 1, then swap the role of x and y
		boolean x_y_role_swapped = (dy > dx);
		if (x_y_role_swapped) {
			x0 = p1.y;
			y0 = p1.x;
			xEnd = p2.y;
			yEnd = p2.x;
			dx = Math.abs(xEnd - x0);
			dy = Math.abs(yEnd - y0);
		}

		// initialize the decision parameter and increments
		int p = 2 * dy - dx;
		int twoDy = 2 * dy, twoDyMinusDx = 2 * (dy - dx);
		int x = x0, y = y0;

		// set step increment to be positive or negative
		int step_x = x0 < xEnd ? 1 : -1;
		int step_y = y0 < yEnd ? 1 : -1;

		// deal with setup for color interpolation first get r,g,b integer values at the end points
		// and the same with z
		int r0 = p1.c.getR_int(), rEnd = p2.c.getR_int();
		int g0 = p1.c.getG_int(), gEnd = p2.c.getG_int();
		int b0 = p1.c.getB_int(), bEnd = p2.c.getB_int();
		int z0 = p1.z, zEnd = p2.z;
		// compute the change in r,g,b, and the same with z
		int dr = Math.abs(rEnd - r0), dg = Math.abs(gEnd - g0), db = Math.abs(bEnd - b0);
		int dz = Math.abs(zEnd - z0);
		// set step increment to be positive or negative, and the same with z
		int step_r = r0 < rEnd ? 1 : -1;
		int step_g = g0 < gEnd ? 1 : -1;
		int step_b = b0 < bEnd ? 1 : -1;
		int step_z = z0 < zEnd ? 1 : -1;
		// compute whole step in each color that is taken each time through loop, and the same with z
		int whole_step_r = step_r * (dr / dx);
		int whole_step_g = step_g * (dg / dx);
		int whole_step_b = step_b * (db / dx);
		int whole_step_z = step_z * (dz / dx);
		// compute remainder, which will be corrected depending on decision parameter, and the same with z
		dr = dr % dx;
		dg = dg % dx;
		db = db % dx;
		dz = dz % dx;
		// initialize decision parameters for red, green, and blue, and the same with z
		int p_r = 2 * dr - dx;
		int twoDr = 2 * dr, twoDrMinusDx = 2 * (dr - dx);
		int r = r0;

		int p_g = 2 * dg - dx;
		int twoDg = 2 * dg, twoDgMinusDx = 2 * (dg - dx);
		int g = g0;

		int p_b = 2 * db - dx;
		int twoDb = 2 * db, twoDbMinusDx = 2 * (db - dx);
		int b = b0;

		int p_z = 2 * dz - dx;
		int twoDz = 2 * dz, twoDzMinusDx = 2 * (dz - dx);
		int z = z0;

		// draw start pixel
		if (x_y_role_swapped) {
			if (x >= 0 && x < buff.getHeight() && y >= 0 && y < buff.getWidth())
				if (z > Zbuff.GetZ(y, x)) {
					buff.setRGB(y, buff.getHeight() - x - 1, (r << 16) | (g << 8) | b);
					Zbuff.SetZ(y, x, z);
				}
		} else {
			if (y >= 0 && y < buff.getHeight() && x >= 0 && x < buff.getWidth())
				if (z > Zbuff.GetZ(x, y)) {
					buff.setRGB(x, buff.getHeight() - y - 1, (r << 16) | (g << 8) | b);
					Zbuff.SetZ(x, y, z);
				}
		}

		while (x != xEnd) {
			// increment x and y
			x += step_x;
			if (p < 0)
				p += twoDy;
			else {
				y += step_y;
				p += twoDyMinusDx;
			}

			// increment r by whole amount slope_r, and correct for accumulated error if needed
			r += whole_step_r;
			if (p_r < 0)
				p_r += twoDr;
			else {
				r += step_r;
				p_r += twoDrMinusDx;
			}

			// increment g by whole amount slope_b, and correct for accumulated error if needed
			g += whole_step_g;
			if (p_g < 0)
				p_g += twoDg;
			else {
				g += step_g;
				p_g += twoDgMinusDx;
			}

			// increment b by whole amount slope_b, and correct for accumulated error if needed
			b += whole_step_b;
			if (p_b < 0)
				p_b += twoDb;
			else {
				b += step_b;
				p_b += twoDbMinusDx;
			}

			// increment z by whole amount slope_z, and correct for accumulated error if needed
			z += whole_step_z;
			if (p_z < 0)
				p_z += twoDz;
			else {
				z += step_z;
				p_z += twoDzMinusDx;
			}

			if (x_y_role_swapped) {
				if (x >= 0 && x < buff.getHeight() && y >= 0 && y < buff.getWidth())
					if (z > Zbuff.GetZ(y, x)) {
						buff.setRGB(y, buff.getHeight() - x - 1, (r << 16) | (g << 8) | b);
						Zbuff.SetZ(y, x, z);
					}
			} else {
				if (y >= 0 && y < buff.getHeight() && x >= 0 && x < buff.getWidth())
					if (z > Zbuff.GetZ(x, y)) {
						buff.setRGB(x, buff.getHeight() - y - 1, (r << 16) | (g << 8) | b);
						Zbuff.SetZ(x, y, z);
					}
			}
		}
	}

	
	// this is use to draw line using Phong. It is almost the same with the draw line, but differen in how we 
	// set color. Phong use normal of every pixel to set color.
	public void drawLineUsePhong(BufferedImage buff, ZBuffer Zbuff, Point2D p1, Point2D p2, Point3D n1, Point3D n2) {
		int x0 = p1.x, y0 = p1.y;
		int xEnd = p2.x, yEnd = p2.y;
		int dx = Math.abs(xEnd - x0), dy = Math.abs(yEnd - y0);
		int r, g, b;
		float alpha = 0;
		int counter = 0;
		Point2D point = new Point2D();

		if (dx == 0 && dy == 0) {
			if (p1.z >= p2.z) {
				drawPoint(buff, Zbuff, p1);
			} else
				drawPoint(buff, Zbuff, p2);
			return;
		}

		// if slope is greater than 1, then swap the role of x and y
		boolean x_y_role_swapped = (dy > dx);
		if (x_y_role_swapped) {
			x0 = p1.y;
			y0 = p1.x;
			xEnd = p2.y;
			yEnd = p2.x;
			dx = Math.abs(xEnd - x0);
			dy = Math.abs(yEnd - y0);
		}

		// initialize the decision parameter and increments
		int p = 2 * dy - dx;
		int twoDy = 2 * dy, twoDyMinusDx = 2 * (dy - dx);
		int x = x0, y = y0;
		Point3D n = new Point3D(n1);
		ColorType C = new ColorType();

		// set step increment to be positive or negative
		int step_x = x0 < xEnd ? 1 : -1;
		int step_y = y0 < yEnd ? 1 : -1;

		// deal with setup for z interpolation
		// first get z integer values at the end points

		int z0 = p1.z, zEnd = p2.z;
		// compute the change in z

		int dz = Math.abs(zEnd - z0);
		// set step increment to be positive or negative

		int step_z = z0 < zEnd ? 1 : -1;
		// compute whole step in each color that is taken each time through loop

		int whole_step_z = step_z * (dz / dx);
		// compute remainder, which will be corrected depending on decision
		// parameter

		dz = dz % dx;
		// initialize decision parameters for z

		int p_z = 2 * dz - dx;
		int twoDz = 2 * dz, twoDzMinusDx = 2 * (dz - dx);
		int z = z0;

		r = p1.c.getR_int();
		g = p1.c.getG_int();
		b = p1.c.getB_int();

		// draw start pixel
		if (x_y_role_swapped) {
			if (x >= 0 && x < buff.getHeight() && y >= 0 && y < buff.getWidth())
				if (z > Zbuff.GetZ(y, x)) {
					buff.setRGB(y, buff.getHeight() - x - 1, (r << 16) | (g << 8) | b);
					Zbuff.SetZ(y, x, z);
				}
		} else {
			if (y >= 0 && y < buff.getHeight() && x >= 0 && x < buff.getWidth())
				if (z > Zbuff.GetZ(x, y)) {
					buff.setRGB(x, buff.getHeight() - y - 1, (r << 16) | (g << 8) | b);
					Zbuff.SetZ(x, y, z);
				}
		}

		while (x != xEnd) {
			// increment x and y
			x += step_x;
			counter += step_x;

			alpha = (float) Math.abs(counter) / (float) dx;

			// we do linear interpolate to get the normal of each pixel
			n.x = alpha * n2.x + (1 - alpha) * n1.x;
			n.y = alpha * n2.y + (1 - alpha) * n1.y;
			n.z = alpha * n2.z + (1 - alpha) * n1.z;

			if (p < 0)
				p += twoDy;
			else {
				y += step_y;
				p += twoDyMinusDx;
			}

			// increment z by whole amount slope_z, and correct for accumulated
			// error if needed
			z += whole_step_z;
			if (p_z < 0)
				p_z += twoDz;
			else {
				z += step_z;
				p_z += twoDzMinusDx;
			}

			if (x_y_role_swapped) {
				point.x = y;
				point.y = x;
				point.z = z;
			} else {
				point.x = x;
				point.y = y;
				point.z = z;
			}
			// get the color under that normal
			C = light.applyLight(mats, view_vector, n, point);

			r = C.getR_int();
			g = C.getG_int();
			b = C.getB_int();

			if (x_y_role_swapped) {
				if (x >= 0 && x < buff.getHeight() && y >= 0 && y < buff.getWidth())
					if (z > Zbuff.GetZ(y, x)) {
						buff.setRGB(y, buff.getHeight() - x - 1, (r << 16) | (g << 8) | b);
						Zbuff.SetZ(y, x, z);
					}
			} else {
				if (y >= 0 && y < buff.getHeight() && x >= 0 && x < buff.getWidth())
					if (z > Zbuff.GetZ(x, y)) {
						buff.setRGB(x, buff.getHeight() - y - 1, (r << 16) | (g << 8) | b);
						Zbuff.SetZ(x, y, z);
					}
			}
		}
	}
	
	//this is the method to draw the line of texture. Actually the difference of these three draw line method is
	//how to set the color of a pixel. For this one we use the color of the texture to set the color by interpolat 
	//the value of u and v
	public void drawLineUseTexture(BufferedImage buff, ZBuffer Zbuff, Point2D p1, Point2D p2) {
		int x0 = p1.x, y0 = p1.y;
		int xEnd = p2.x, yEnd = p2.y;
		int dx = Math.abs(xEnd - x0), dy = Math.abs(yEnd - y0);

		if (dx == 0 && dy == 0) {
			if (p1.z >= p2.z) {
				drawPoint(buff, Zbuff, p1);
			} else
				drawPoint(buff, Zbuff, p2);
			return;
		}

		// if slope is greater than 1, then swap the role of x and y
		boolean x_y_role_swapped = (dy > dx);
		if (x_y_role_swapped) {
			x0 = p1.y;
			y0 = p1.x;
			xEnd = p2.y;
			yEnd = p2.x;
			dx = Math.abs(xEnd - x0);
			dy = Math.abs(yEnd - y0);
		}

		// initialize the decision parameter and increments
		int p = 2 * dy - dx;
		int twoDy = 2 * dy, twoDyMinusDx = 2 * (dy - dx);
		int x = x0, y = y0;

		// set step increment to be positive or negative
		int step_x = x0 < xEnd ? 1 : -1;
		int step_y = y0 < yEnd ? 1 : -1;
		// set u,v
		Point2D pixel = new Point2D(p1);
		float du = (p2.u - p1.u) / (float) dx;
		float dv = (p2.v - p1.v) / (float) dx;
		int r, g, b;
		// deal with setup for z interpolation
		// first get z integer values at the end points

		int z0 = p1.z, zEnd = p2.z;
		// compute the change in z

		int dz = Math.abs(zEnd - z0);
		// set step increment to be positive or negative

		int step_z = z0 < zEnd ? 1 : -1;
		// compute whole step in each color that is taken each time through loop

		int whole_step_z = step_z * (dz / dx);
		// compute remainder, which will be corrected depending on decision
		// parameter
		dz = dz % dx;
		// initialize decision parameters for z

		int p_z = 2 * dz - dx;
		int twoDz = 2 * dz, twoDzMinusDx = 2 * (dz - dx);
		int z = z0;

		r = p1.c.getR_int();
		g = p1.c.getG_int();
		b = p1.c.getB_int();

		// draw start pixel
		if (x_y_role_swapped) {
			if (x >= 0 && x < buff.getHeight() && y >= 0 && y < buff.getWidth())
				if (z > Zbuff.GetZ(y, x)) {
					buff.setRGB(y, buff.getHeight() - x - 1, (r << 16) | (g << 8) | b);
					Zbuff.SetZ(y, x, z);
				}
		} else {
			if (y >= 0 && y < buff.getHeight() && x >= 0 && x < buff.getWidth())
				if (z > Zbuff.GetZ(x, y)) {
					buff.setRGB(x, buff.getHeight() - y - 1, (r << 16) | (g << 8) | b);
					Zbuff.SetZ(x, y, z);
				}
		}

		while (x != xEnd) {
			// increment x and y
			x += step_x;
			if (p < 0)
				p += twoDy;
			else {
				y += step_y;
				p += twoDyMinusDx;
			}

			// increment z by whole amount slope_z, and correct for accumulated
			// error if needed
			z += whole_step_z;
			if (p_z < 0)
				p_z += twoDz;
			else {
				z += step_z;
				p_z += twoDzMinusDx;
			}
			// linear interpolate u,v
			pixel.u += du;
			pixel.v += dv;
			// if(pixel.u < 0)
			// {pixel.u = 0;}
			searchColor(pixel);

			r = pixel.c.getR_int();
			g = pixel.c.getG_int();
			b = pixel.c.getB_int();

			if (x_y_role_swapped) {
				if (x >= 0 && x < buff.getHeight() && y >= 0 && y < buff.getWidth())
					if (z > Zbuff.GetZ(y, x)) {
						buff.setRGB(y, buff.getHeight() - x - 1, (r << 16) | (g << 8) | b);
						Zbuff.SetZ(y, x, z);
					}
			} else {
				if (y >= 0 && y < buff.getHeight() && x >= 0 && x < buff.getWidth())
					if (z > Zbuff.GetZ(x, y)) {
						buff.setRGB(x, buff.getHeight() - y - 1, (r << 16) | (g << 8) | b);
						Zbuff.SetZ(x, y, z);
					}
			}
		}
	}

	/**********************************************************************
	 * Draws a filled triangle. The triangle may be filled using flat fill or
	 * smooth fill. This routine fills columns of pixels within the left-hand
	 * part, and then the right-hand part of the triangle.
	 * 
	 *   
	 *	                         *
	 *	                        /|\
	 *	                       / | \
	 *	                      /  |  \
	 *	                     *---|---*
	 *	            left-hand       right-hand
	 *	              part             part
	 *
	 * @param buff
	 *            Buffer object.
	 * @param Zbuff
	 * 			  ZBuffer object
	 * @param p1
	 *            First given vertex of the triangle.
	 * @param p2
	 *            Second given vertex of the triangle.
	 * @param p3
	 *            Third given vertex of the triangle.
	 * @param do_smooth
	 *            Flag indicating whether flat fill or smooth fill should be
	 *            used.
	 * @param mode
	 * 			  to deside which rendering method should be used
	 */
	public void drawTriangle(BufferedImage buff, ZBuffer Zbuff, Point2D p1, Point2D p2, Point2D p3, int mode) {
		// sort the triangle vertices by ascending x value
		Point3D vpts[] = { normal1, normal2, normal3 };
		Point2D p[] = sortTriangleVerts(p1, p2, p3, vpts);
		//  for texture mapping
		if (mode == 4) {
			searchColor(p1);
			searchColor(p2);
			searchColor(p3);
		}

		int x;
		float y_a, y_b, z_a, z_b;
		float dy_a, dy_b, dz_a, dz_b, dx_a, dx_b;
		float du_a = 0, du_b = 0, dv_a = 0, dv_b = 0;
		float dr_a = 0, dg_a = 0, db_a = 0, dr_b = 0, dg_b = 0, db_b = 0;
		float alpha_a = 0, alpha_b = 0;

		Point2D side_a = new Point2D(p[0]), side_b = new Point2D(p[0]);
		Point3D side_an = new Point3D(vpts[0]), side_bn = new Point3D(vpts[0]);

		dx_a = (float) (p[1].x - p[0].x);
		dx_b = (float) (p[2].x - p[0].x);

		y_b = p[0].y;
		dy_b = ((float) (p[2].y - p[0].y)) / (p[2].x - p[0].x);

		z_b = p[0].z;
		dz_b = ((float) (p[2].z - p[0].z)) / (p[2].x - p[0].x);

		if (mode == 1) {
			// calculate slopes in r, g, b for segment b
			dr_b = ((float) (p[2].c.r - p[0].c.r)) / (p[2].x - p[0].x);
			dg_b = ((float) (p[2].c.g - p[0].c.g)) / (p[2].x - p[0].x);
			db_b = ((float) (p[2].c.b - p[0].c.b)) / (p[2].x - p[0].x);
		}

		if (mode == 2) {
			side_a.c = new ColorType(p1.c);
			side_b.c = new ColorType(p1.c);
		}

		if (mode == 4) {
			du_b = (p[2].u - p[0].u) / (p[2].x - p[0].x);
			dv_b = (p[2].v - p[0].v) / (p[2].x - p[0].x);
		}

		// if there is a left-hand part to the triangle then fill it
		if (p[0].x != p[1].x) {
			y_a = p[0].y;
			dy_a = ((float) (p[1].y - p[0].y)) / (p[1].x - p[0].x);

			z_a = p[0].z;
			dz_a = ((float) (p[1].z - p[0].z)) / (p[1].x - p[0].x);

			if (mode == 1) {
				// calculate slopes in r, g, b for segment a
				dr_a = ((float) (p[1].c.r - p[0].c.r)) / (p[1].x - p[0].x);
				dg_a = ((float) (p[1].c.g - p[0].c.g)) / (p[1].x - p[0].x);
				db_a = ((float) (p[1].c.b - p[0].c.b)) / (p[1].x - p[0].x);
			}

			if (mode == 4) {
				du_a = (p[1].u - p[0].u) / (p[1].x - p[0].x);
				dv_a = (p[1].v - p[0].v) / (p[1].x - p[0].x);
			}

			// loop over the columns for left-hand part of triangle
			// filling from side a to side b of the span
			for (x = p[0].x; x < p[1].x; ++x) {

				if (mode == 3)
					drawLineUsePhong(buff, Zbuff, side_a, side_b, side_an, side_bn);
				else if (mode == 4)
					drawLineUseTexture(buff, Zbuff, side_a, side_b);
				else
					drawLine(buff, Zbuff, side_a, side_b);

				++side_a.x;
				++side_b.x;
				y_a += dy_a;
				y_b += dy_b;
				z_a += dz_a;
				z_b += dz_b;
				side_a.y = (int) y_a;
				side_b.y = (int) y_b;
				side_a.z = (int) z_a;
				side_b.z = (int) z_b;
				if (mode == 1) {
					side_a.c.r += dr_a;
					side_b.c.r += dr_b;
					side_a.c.g += dg_a;
					side_b.c.g += dg_b;
					side_a.c.b += db_a;
					side_b.c.b += db_b;
				}

				if (mode == 3) {
					alpha_a = (float) (side_a.x - p[0].x) / dx_a;
					alpha_b = (float) (side_b.x - p[0].x) / dx_b;

					side_an.x = alpha_a * vpts[1].x + (1 - alpha_a) * vpts[0].x;
					side_an.y = alpha_a * vpts[1].y + (1 - alpha_a) * vpts[0].y;
					side_an.z = alpha_a * vpts[1].z + (1 - alpha_a) * vpts[0].z;

					side_bn.x = alpha_b * vpts[2].x + (1 - alpha_b) * vpts[0].x;
					side_bn.y = alpha_b * vpts[2].y + (1 - alpha_b) * vpts[0].y;
					side_bn.z = alpha_b * vpts[2].z + (1 - alpha_b) * vpts[0].z;

					side_a.c = light.applyLight(mats, view_vector, side_an, side_a);
					side_b.c = light.applyLight(mats, view_vector, side_bn, side_b);
				}

				if (mode == 4 && x < p[1].x - 1) {
					side_a.u += du_a;
					side_a.v += dv_a;
					side_b.u += du_b;
					side_b.v += dv_b;
					searchColor(side_a);
					searchColor(side_b);
				}

			}
		}

		// there is no right-hand part of triangle
		if (p[1].x == p[2].x)
			return;
		
		side_a = new Point2D(p[1]);
		side_an = new Point3D(vpts[1]);
		dx_a = p[2].x - p[1].x;

		y_a = p[1].y;
		dy_a = ((float) (p[2].y - p[1].y)) / (p[2].x - p[1].x);

		z_a = p[1].z;
		dz_a = ((float) (p[2].z - p[1].z)) / (p[2].x - p[1].x);

		if (mode == 1) {
			// calculate slopes in r, g, b for replacement for segment a
			dr_a = ((float) (p[2].c.r - p[1].c.r)) / (p[2].x - p[1].x);
			dg_a = ((float) (p[2].c.g - p[1].c.g)) / (p[2].x - p[1].x);
			db_a = ((float) (p[2].c.b - p[1].c.b)) / (p[2].x - p[1].x);
		}

		if (mode == 2)
			side_a.c = new ColorType(p1.c);

		if (mode == 4) {
			du_a = (p[2].u - p[1].u) / (p[2].x - p[1].x);
			dv_a = (p[2].v - p[1].v) / (p[2].x - p[1].x);
		}

		// loop over the columns for right-hand part of triangle
		// filling from side a to side b of the span
		for (x = p[1].x; x <= p[2].x; ++x) {

			if (mode == 3)
				drawLineUsePhong(buff, Zbuff, side_a, side_b, side_an, side_bn);
			else if (mode == 4)
				drawLineUseTexture(buff, Zbuff, side_a, side_b);
			else
				drawLine(buff, Zbuff, side_a, side_b);

			++side_a.x;
			++side_b.x;
			y_a += dy_a;
			y_b += dy_b;
			z_a += dz_a;
			z_b += dz_b;
			side_a.y = (int) y_a;
			side_b.y = (int) y_b;
			side_a.z = (int) z_a;
			side_b.z = (int) z_b;

			if (mode == 1) {
				side_a.c.r += dr_a;
				side_b.c.r += dr_b;
				side_a.c.g += dg_a;
				side_b.c.g += dg_b;
				side_a.c.b += db_a;
				side_b.c.b += db_b;
			}

			else if (mode == 3) {

				alpha_a = (side_a.x - p[1].x) / dx_a;
				alpha_b = (side_b.x - p[0].x) / dx_b;

				side_an.x = alpha_a * vpts[2].x + (1 - alpha_a) * vpts[1].x;
				side_an.y = alpha_a * vpts[2].y + (1 - alpha_a) * vpts[1].y;
				side_an.z = alpha_a * vpts[2].z + (1 - alpha_a) * vpts[1].z;

				side_bn.x = alpha_b * vpts[2].x + (1 - alpha_b) * vpts[0].x;
				side_bn.y = alpha_b * vpts[2].y + (1 - alpha_b) * vpts[0].y;
				side_bn.z = alpha_b * vpts[2].z + (1 - alpha_b) * vpts[0].z;

				side_a.c = light.applyLight(mats, view_vector, side_an, side_a);
				side_b.c = light.applyLight(mats, view_vector, side_bn, side_b);
			}

			if (mode == 4 && x < p[2].x - 1) {
				side_a.u += du_a;
				side_a.v += dv_a;
				side_b.u += du_b;
				side_b.v += dv_b;
				searchColor(side_a);
				searchColor(side_b);
			}
		}
	}

	/**********************************************************************
	 * Helper function to bubble sort triangle vertices by ascending x value.
	 * 
	 * @param p1
	 *            First given vertex of the triangle.
	 * @param p2
	 *            Second given vertex of the triangle.
	 * @param p3
	 *            Third given vertex of the triangle.
	 * @param vpts
	 * 			  is used to switch the normal
	 * 
	 * @return Array of 3 points, sorted by ascending x value.
	 */
	private Point2D[] sortTriangleVerts(Point2D p1, Point2D p2, Point2D p3, Point3D[] vpts) {
		Point2D pts[] = { p1, p2, p3 };
		Point2D tmp;
		Point3D vtmp;
		int j = 0;
		boolean swapped = true;

		while (swapped) {
			swapped = false;
			j++;
			for (int i = 0; i < 3 - j; i++) {
				if (pts[i].x > pts[i + 1].x) {
					tmp = pts[i];
					pts[i] = pts[i + 1];
					pts[i + 1] = tmp;

					vtmp = vpts[i];
					vpts[i] = vpts[i + 1];
					vpts[i + 1] = vtmp;

					swapped = true;
				}
			}
		}
		return (pts);
	}

	// set parameter for what phong could use
	public void setPhong(AllLight l, Material m, Point3D v, Point3D n0, Point3D n1, Point3D n2) {
		light = l;
		mats = m;
		view_vector = v;
		normal1 = new Point3D(n0);
		normal2 = new Point3D(n1);
		normal3 = new Point3D(n2);
	}

	public void setTexture(BufferedImage texture) {
		this.texture = texture;
	}
	
	// this method is based on u,v to find the color of the pixel
	public void searchColor(Point2D p) {
		int u, v;
		int[] pixel = new int[4], r = new int[4], g = new int[4], b = new int[4];
		float[] rm = new float[2], bm = new float[2], gm = new float[2];

		u = (int) Math.floor(p.u);
		v = (int) Math.floor(p.v);

		if (u < 0 && u >= -1)
			u = 0;// because the computation is not so precise, so some point
					// many less than 0, and we just assign 0 to them
		if (v < 0 && v >= -1)
			v = 0;// but if less than -1, that means.... there is bug...
		if (u < texture.getWidth() - 1 && v < texture.getHeight() - 1) {
			// do three times linear interpolation to get the right color
			pixel[0] = texture.getRGB(u, v);
			pixel[1] = texture.getRGB(u + 1, v);
			pixel[2] = texture.getRGB(u, v + 1);
			pixel[3] = texture.getRGB(u + 1, v + 1);
			r[0] = ((pixel[0] & 0x00ff0000) >> 16);
			r[1] = ((pixel[1] & 0x00ff0000) >> 16);
			r[2] = ((pixel[2] & 0x00ff0000) >> 16);
			r[3] = ((pixel[3] & 0x00ff0000) >> 16);
			g[0] = (pixel[0] & 0x0000ff00) >> 8;
			g[1] = (pixel[1] & 0x0000ff00) >> 8;
			g[2] = (pixel[2] & 0x0000ff00) >> 8;
			g[3] = (pixel[3] & 0x0000ff00) >> 8;
			b[0] = pixel[0] & 0x000000ff;
			b[1] = pixel[1] & 0x000000ff;
			b[2] = pixel[2] & 0x000000ff;
			b[3] = pixel[3] & 0x000000ff;
			rm[0] = (float) (r[2] - r[0]) * (p.v - v) + r[0];
			gm[0] = (float) (g[2] - g[0]) * (p.v - v) + g[0];
			bm[0] = (float) (b[2] - b[0]) * (p.v - v) + b[0];
			rm[1] = (float) (r[3] - r[1]) * (p.v - v) + r[1];
			gm[1] = (float) (g[3] - g[1]) * (p.v - v) + g[1];
			bm[1] = (float) (b[3] - b[1]) * (p.v - v) + b[1];
			p.c.r = ((rm[1] - rm[0]) * (p.u - u) + rm[0]) / 255;
			p.c.g = ((gm[1] - gm[0]) * (p.u - u) + gm[0]) / 255;
			p.c.b = ((bm[1] - bm[0]) * (p.u - u) + bm[0]) / 255;
		} else { 
			// the some reason, some point may bigger than the upper bound, we just chose the upper bound
			pixel[0] = texture.getRGB(u, v);
			p.c.r = (float) ((pixel[0] & 0x00ff0000) >> 16) / 255;
			p.c.g = (float) ((pixel[0] & 0x0000ff00) >> 8) / 255;
			p.c.b = (float) (pixel[0] & 0x000000ff) / 255;
		}
	}

}