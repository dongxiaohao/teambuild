package cn.neu.kou.teambuild.main;

import cn.neu.kou.teambuild.cpi.CpiCorelateTreeNode;
import cn.neu.kou.teambuild.cpi.CpiTreeNode;
import cn.neu.kou.teambuild.cpi.ValidateTeam;

import java.util.HashMap;
import java.util.Set;

public class MinCostTeam {
    private Criteria criteria;
    private ValidateTeam minStructRestrictTeam;
    private int minStructRestrictCost;

    public MinCostTeam(Criteria criteria) {
        this.criteria = criteria;
    }

    public MinCostTeam(Criteria criteria, ValidateTeam minStructRestrictTeam) {
        this.minStructRestrictTeam = minStructRestrictTeam;
        this.criteria = criteria;
    }

    public ValidateTeam minStructRestrictTeam(Set<ValidateTeam> teamSet) {
        ValidateTeam minValidateTeam = null;
        int curMin = Integer.MAX_VALUE;
        int min = Integer.MAX_VALUE;
        int[][] searchEdge = criteria.getSearchGraphEdge();
        for(ValidateTeam validateTeam : teamSet) {

            //重新再次计算当前的结构和约束
            curMin = minStructRestrict(searchEdge, validateTeam);
//            if(curMin > 0 && curMin < min) {
//                min = curMin;
//                minValidateTeam = validateTeam;
//            }

            //在查询图中结果是对的
            if(validateTeam.getTotalWeight() < min) {
                min = (int) (validateTeam.getTotalWeight()+0.5);
                minValidateTeam = validateTeam;
            }
        }
        this.minStructRestrictTeam = minValidateTeam;
        this.minStructRestrictCost = min;
        return minValidateTeam;
    }

    public int minStructRestrict(int[][] searchGraphEdge, ValidateTeam validateTeam) {
        int sum = 0;
        int weight;
        for(int i=0; i<searchGraphEdge.length; i++) {
            if((weight = validateTeam.edgeWeight(searchGraphEdge[i][0], searchGraphEdge[i][1])) > 0) {
                sum += weight;
            }
            else {
                sum = -1;
                break;
            }
        }
        return sum;
    }

//    public void getMinStructRestricTeam(Set<ValidateTeam> teamSet) {
//        this.minStructRestrictCost = Integer.MAX_VALUE;
//        int curCost;
//        for(ValidateTeam team : teamSet) {
//            curCost = criteria.minStructRestrict(team.getRoleIdUserIdMap());
//            if(curCost < this.minStructRestrictCost) {
//                this.minStructRestrictCost = curCost;
//                this.minStructRestrictTeam = team;
//            }
//        }
//    }

    public void printMinStructRestricTeamAllCriteria() {
        //System.out.println("Min diameter: " + criteria.minDiameter(this.minStructRestrictTeam.getUserIds()));
       // System.out.println("Min struct sum: " + criteria.minStructSum(this.minStructRestrictTeam.getUserIds()));
        //System.out.println("Min build tree: " + criteria.minBuildTree(criteria.build_matrix(this.minStructRestrictTeam.getRoleIdUserIdMap())));
        System.out.println("Min struct restrict: " + this.minStructRestrictCost);
    }
}
