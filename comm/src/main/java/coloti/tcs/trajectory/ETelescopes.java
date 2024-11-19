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

import java.util.HashMap;
import java.util.Map;


public enum ETelescopes{
    ASTRI1(1),
    ASTRI2(2),
    ASTRI3(3),
    ASTRI4(4),
    ASTRI5(5),
    ASTRI6(6),
    ASTRI7(7),
    ASTRI8(8),
    ASTRI9(9),
    COLOTI(10);

    private final int value;
    private final String[] names = { "ASTRI1" ,"ASTRI2", "ASTRI3", "ASTRI4", "ASTRI5", "ASTRI6", "ASTRI7",
            "ASTRI8", "ASTRI9", "COLOTI"};

    private ETelescopes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private static Map map = new HashMap<>();
    static {
        for (ETelescopes pmc : ETelescopes.values()) {
            map.put(pmc.value, pmc);
        }
    }

    public static ETelescopes valueOf(int pmc) {
        return (ETelescopes) map.get(pmc);
    }

    public String getTelName() {
        return names[value - 1];
    }

}
