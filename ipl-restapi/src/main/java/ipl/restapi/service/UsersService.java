package ipl.restapi.service;

import ipl.manager.pojo.UserInfo;

/**
 * <p>Descirption:</p>
 *
 * @author 王海
 * @version V1.0
 * @package ipl.restapi.service
 * @date 2018/3/16 21:09
 * @since api1.0
 */
public interface UsersService {
    /**
     * 通过用户id查询User所有数据的接口
     *
     * @param userId 用户id
     * @return 用户
     */
    UserInfo getUserById(long userId);
}
