package com.lytech.anoyoce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lytech.anoyoce.domain.entity.Menu;


import java.util.List;

/**
 * @author 吴嘉豪
 * @date 2023/11/15 14:07
 */
public interface MenuMapper extends BaseMapper<Menu> {
    List<String> selectPermsByUserId(Long userid);
}
