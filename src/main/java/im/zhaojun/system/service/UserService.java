package im.zhaojun.system.service;

import com.github.pagehelper.PageHelper;
import im.zhaojun.common.exception.DuplicateNameException;
import im.zhaojun.common.shiro.ShiroActionProperties;
import im.zhaojun.common.util.TreeUtil;
import im.zhaojun.system.mapper.UserMapper;
import im.zhaojun.system.mapper.UserRoleMapper;
import im.zhaojun.system.model.Menu;
import im.zhaojun.system.model.User;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private MenuService menuService;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private UserAuthsService userAuthsService;

    @Resource
    private SessionDAO sessionDAO;

    @Resource
    private ShiroActionProperties shiroActionProperties;

    public List<User> selectAllWithDept(int page, int rows, User userQuery) {
        PageHelper.startPage(page, rows);
        return userMapper.selectAllWithDept(userQuery);
    }

    public Integer[] selectRoleIdsById(Integer userId) {
        return userMapper.selectRoleIdsByUserId(userId);
    }

    @Transactional
    public Integer add(User user, Integer[] roleIds) {
        checkUserNameExistOnCreate(user.getUsername());
        String salt = generateSalt();
        String encryptPassword = new Md5Hash(user.getPassword(), salt).toString();

        user.setSalt(salt);
        user.setPassword(encryptPassword);
        userMapper.insert(user);

        grantRole(user.getUserId(), roleIds);

        return user.getUserId();
    }

    public void updateLastLoginTimeByUsername(String username) {
        userMapper.updateLastLoginTimeByUsername(username);
    }

    public boolean disableUserByID(Integer id) {
//        offlineByUserId(id); // ??????????????????, ???????????????, ???????????????????????????????????????.
        return userMapper.updateStatusByPrimaryKey(id, 0) == 1;
    }

    public boolean enableUserByID(Integer id) {
        return userMapper.updateStatusByPrimaryKey(id, 1) == 1;
    }

    /**
     * ???????????? ID ????????????.
     * @param userId    ?????? ID
     */
    public void activeUserByUserId(Integer userId) {
        userMapper.activeUserByUserId(userId);
    }

    @Transactional
    public boolean update(User user, Integer[] roleIds) {
        checkUserNameExistOnUpdate(user);
        grantRole(user.getUserId(), roleIds);
        return userMapper.updateByPrimaryKeySelective(user) == 1;
    }

    public User selectOne(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    /**
     * ????????????????????????????????????
     * @param username  ?????????
     */
    public void checkUserNameExistOnCreate(String username) {
        if (userMapper.countByUserName(username) > 0) {
            throw new DuplicateNameException();
        }
    }

    public void checkUserNameExistOnUpdate(User user) {
        if (userMapper.countByUserNameNotIncludeUserId(user.getUsername(), user.getUserId()) > 0) {
            throw new DuplicateNameException();
        }
    }

    public void offlineBySessionId(String sessionId) {
        Session session = sessionDAO.readSession(sessionId);
        if (session != null) {
            log.debug("???????????? sessionId ??? :" + sessionId + "?????????.");
            session.stop();
            sessionDAO.delete(session);
        }
    }

    /**
     * ????????????????????????????????????
     */
    public void offlineByUserId(Integer userId) {
        Collection<Session> activeSessions = sessionDAO.getActiveSessions();
        for (Session session : activeSessions) {
            SimplePrincipalCollection simplePrincipalCollection = (SimplePrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            if (simplePrincipalCollection != null) {
                User user = (User) simplePrincipalCollection.getPrimaryPrincipal();
                if (user != null && userId.equals(user.getUserId())) {
                    offlineBySessionId(String.valueOf(session.getId()));
                }
            }
        }
    }

    @Transactional
    public void grantRole(Integer userId, Integer[] roleIds) {
        if (roleIds == null || roleIds.length == 0) {
            throw new IllegalArgumentException("?????????????????????????????????.");
        }
        // ?????????????????????, ???????????????.
        userRoleMapper.deleteUserRoleByUserId(userId);
        userRoleMapper.insertList(userId, roleIds);
    }

    public User selectByActiveCode(String activeCode) {
        return userMapper.selectByActiveCode(activeCode);
    }

    public int count() {
        return userMapper.count();
    }

    @Transactional
    public void delete(Integer userId) {
        // ???????????????????????????????????????, ?????????, ??????????????????.
        User user = userMapper.selectByPrimaryKey(userId);
        if (shiroActionProperties.getSuperAdminUsername().equals(user.getUsername())) {
            throw new UnauthorizedException("???????????????????????????, ?????????.");
        }
        userAuthsService.deleteByUserId(userId);
        userMapper.deleteByPrimaryKey(userId);
        userRoleMapper.deleteUserRoleByUserId(userId);
    }

    /**
     * ??????????????????????????????????????????????????????.
     * @param username      ?????????
     * @return              ????????????????????????
     */
    public Set<String> selectPermsByUsername(String username) {
        Set<String> perms = new HashSet<>();
        List<Menu> menuTreeVOS = menuService.selectMenuTreeVOByUsername(username);
        List<Menu> leafNodeMenuList = TreeUtil.getAllLeafNode(menuTreeVOS);
        for (Menu menu : leafNodeMenuList) {
            perms.add(menu.getPerms());
        }
        perms.addAll(userMapper.selectOperatorPermsByUserName(username));
        return perms;
    }

    public Set<String> selectRoleNameByUserName(String username) {
        return userMapper.selectRoleNameByUserName(username);
    }

    public User selectOneByUserName(String username) {
        return userMapper.selectOneByUserName(username);
    }

    public void updatePasswordByUserId(Integer userId, String password) {
        String salt = generateSalt();
        String encryptPassword = new Md5Hash(password, salt).toString();
        userMapper.updatePasswordByUserId(userId, encryptPassword, salt);
    }

    private String generateSalt() {
        return String.valueOf(System.currentTimeMillis());
    }
}