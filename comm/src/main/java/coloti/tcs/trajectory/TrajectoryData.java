package astri.trajectory;

/***************************************************************************
* Copyright (C) 2022 INAF
* 
* This program is free software: you can redistribute it and/or modify it 
* under the terms of the GNU Lesser General Public License as published by 
* the Free Software Foundation, either version 3 of the License
* or (at your option) any later version. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
* the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
* See the GNU Lesser General Public License for more details. 
* You should have received a copy of the GNU Lesser General Public License 
* along with this program. If not, see <https://www.gnu.org/licenses/>.
* 
* Authors:
* 
*  Gino Tosti UNIPG/INAF gino.tosti@unipg.it 
*****************************************************************************/

import java.util.LinkedList;
import java.util.List;

import org.jastronomy.jsofa.JSOFA.JulianDate;

import astri.astron.PointingCoord;
import astri.astron.TimeUtil;

public class TrajectoryData {
	private JulianDate time; // Jd The time when the telescope should be at
	private double az = 0; // The azimuth in deg
	private double el = 0; // The elevation in deg
	private double za = 0;
	private double refCorr = 0;
	private double tpAzCorr = 0;
	private double tpElCorr = 0;
	private PointingCoord coo;

	// this position and moving at this velocity. number of milliseconds since
	// January 1, 1970, 00:00:00 GMT

	public TrajectoryData() {

	}

	public TrajectoryData(final JulianDate time, final double az, final double el) {
		this.time = time;
		this.az = az;
		this.el = el;
		this.za = 90 - el;
	}

	public double getAz() {
		return az;
	}

	public void setAz(final double az) {
		this.az = az;
	}

	public double getEl() {
		return el;
	}

	public double getElAsRadians() {
		return Math.toRadians(el);
	}

	public double getAzAsRadians() {
		return Math.toRadians(az);
	}

	public void setEl(final double el) {
		this.el = el;
	}

	public JulianDate getTime() {
		return time;
	}

	public void setTime(final JulianDate time) {
		this.time = time;
	}

	public double getZa() {
		return za;
	}

	public void setZa(final double za) {
		this.za = za;
	}

	public double getRefCorr() {
		return refCorr;
	}

	public void setRefCorr(final double refCorr) {
		this.refCorr = refCorr;
	}

	public double getTpAzCorr() {
		return tpAzCorr;
	}

	public void setTpAzCorr(final double tpAzCorr) {
		this.tpAzCorr = tpAzCorr;
	}

	public double getTpElCorr() {
		return tpElCorr;
	}

	public void setTpElCorr(final double tpElCorr) {
		this.tpElCorr = tpElCorr;
	}

	@Override
	public String toString() {
		return "TrajectoryData [time=" + (time.djm0 + time.djm1) + ", az=" + az + ", el=" + el + ", za=" + za
				+ ", refCorr="
				+ refCorr + ", tpAzCorr=" + tpAzCorr + ", tpElCorr=" + tpElCorr + "]";
	}

	public static List<TrajectoryData> arrayToList(final double[] data) {
		final List<TrajectoryData> tra = new LinkedList<>();
		for (int i = 0; i < data.length; i += 3) {
			final TrajectoryData tr = new TrajectoryData(TimeUtil.jdToJulianDate(data[i]), data[i + 1], data[i + 2]);
			tra.add(tr);
		}
		return tra;
	}

	public static double[] arrayToList(final List<TrajectoryData> data) {
		final double[] tra = new double[data.size() * 3];
		int i = 0;
		for (final TrajectoryData dat : data) {
			tra[i] = TimeUtil.JulianDateToJd(dat.getTime());
			tra[i + 1] = dat.getAz();
			tra[i + 2] = dat.getEl();
			i += 3;
		}
		return tra;
	}

	public PointingCoord getCoo() {
		return coo;
	}

	public void setCoo(PointingCoord coo) {
		this.coo = coo;
	}

}
