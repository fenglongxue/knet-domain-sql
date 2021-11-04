package cn.knet.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OldAndNewVo{
    String key;
    Object oldValue;
    Object newValue;
    boolean isUpdate=false;
}
