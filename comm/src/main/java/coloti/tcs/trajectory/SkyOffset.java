
package coloti.tcs.trajectory;

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

public class SkyOffset {

	private double offsetElevation=0.0;
	private double offsetAzimuth=0.0;


	public SkyOffset(double offsetElevation, double offsetAzimuth) {
		this.offsetElevation = offsetElevation;
		this.offsetAzimuth = offsetAzimuth;
	}
	public double getOffsetElevation() {
		return offsetElevation;
	}
	public void setOffsetElevation(double offsetElevation) {
		this.offsetElevation = offsetElevation;
	}
	public double getOffsetAzimuth() {
		return offsetAzimuth;
	}
	public void setOffsetAzimuth(double offsetAzimuth) {
		this.offsetAzimuth = offsetAzimuth;
	}
	@Override
	public String toString() {
		return "SkyOffset [offsetElevation=" + offsetElevation + ", offsetAzimuth=" + offsetAzimuth + "]";
	}



	

	
	
}
