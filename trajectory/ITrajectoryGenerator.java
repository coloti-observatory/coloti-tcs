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

import java.util.List;

import org.jastronomy.jsofa.JSOFA.JulianDate;

import astri.astron.Target;
import astri.astron.Weather;

public interface ITrajectoryGenerator {
	
	/**
	 * @param startTime
	 * @return
	 */
	List<TrajectoryData> getTrajectory(Target source, SkyOffset offsets, long startTime,long timeStep);

	/**
	 * @param source
	 * @param startTime
	 * @param timeStep
	 * @return
	 */
	List<TrajectoryData> getTrajectory(Weather atm, JulianDate startTime);
	
	/**
	 * @param source
	 * @param startTime
	 * @return
	 */
	TrajectoryData getHorizontal(Target source,long startTime);
	
	/**
	 * @param ra  //apparent ra
	 * @param dec //apparent dec 
	 * @param startTime
	 * @return
	 */
	TrajectoryData getHorizontal(double ra,double dec, long startTime);

}
