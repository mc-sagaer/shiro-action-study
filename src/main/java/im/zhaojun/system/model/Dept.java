package im.zhaojun.system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Dept implements Serializable {

    private static final long serialVersionUID = -194076170058276436L;

    /**
 	* 部门ID
	*/
    // @JsonProperty 此注解用于属性上，作用是把该属性的名称序列化为另外一个名称，如把trueName属性序列化为name，
    @JsonProperty("id")
    private Integer deptId;

    /**
	* 部门名称
	*/
    @JsonProperty("name")
    private String deptName;

    /**
	* 上级部门 ID
	*/
    private Integer parentId;

    /**
	* 排序
	*/
    private Integer orderNum;

    /**
	* 创建时间
	*/

    //注解名称：@JsonIgnore
    //作用：在实体类向前台返回数据时用来忽略不想传递给前台的属性或接口。
    //Eg：Bean实体中会有某些运维字段，在返回信息给前台的时候，当不希望将对应值也一并返回；
    //　　此时可以在对应属性上加上注解JsonIgnore或者，可以在User类上加上注解@JsonIgnoreProperties(value = "{password}")
    @JsonIgnore
    private Date createTime;

    /**
	* 修改时间
	*/
    @JsonIgnore
    private Date modifyTime;

    // @JsonInclude(value=Include.NON_NULL)  是用再实体类的方法类的头上  作用是实体类的参数查询到的为null的不显示
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Dept> children;

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public List<Dept> getChildren() {
        return children;
    }

    public void setChildren(List<Dept> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Dept{" +
                "deptId=" + deptId +
                ", deptName='" + deptName + '\'' +
                ", parentId=" + parentId +
                ", orderNum=" + orderNum +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", children=" + children +
                '}';
    }
}