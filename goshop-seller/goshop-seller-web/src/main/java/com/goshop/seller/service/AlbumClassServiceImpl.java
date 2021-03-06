package com.goshop.seller.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.goshop.manager.i.StoreService;
import com.goshop.manager.mapper.AlbumClassMapper;
import com.goshop.manager.mapper.AlbumPicMapper;
import com.goshop.manager.pojo.AlbumClass;
import com.goshop.manager.pojo.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class AlbumClassServiceImpl implements AlbumClassService {

    @Autowired
    AlbumClassMapper albumClassMapper;

    @Autowired
    AlbumPicMapper albumPicMapper;

    @Autowired
    StoreService storeService;

    @Override
    public PageInfo<AlbumClass> findByStoreId(Integer curPage, Integer pageSize, Long memberId,Integer sortValue) {
        if(curPage==null){
            curPage=1;
        }
        if(pageSize==null){
            pageSize=20;
        }
        PageHelper.startPage(curPage, pageSize);
        List<AlbumClass> list=albumClassMapper.findByStoreId(memberId,sortValue);
        return new PageInfo<>(list);
    }

    @Override
    public Integer findCountByStoreId(Long userId) {
        return albumClassMapper.findCountByStoreId(userId);
    }

    @Override
    public AlbumClass findOneByAclassNameAndStoreId(String aclassName, Long userId) {
        return albumClassMapper.findOneByAclassNameAndStoreId(aclassName, userId);
    }

    @Override
    public int save(AlbumClass albumClass,Long userId) {
        Store store=storeService.findByMemberId(userId);
        albumClass.setStoreId(store.getStoreId());
        return albumClassMapper.insertSelective(albumClass);
    }

    @Override
    public int delete(Integer aclassId,Long userId) {
        AlbumClass albumClass=albumClassMapper.findDefaultAlbumClass(userId);
        Assert.notNull(albumClass,"没有默认相册异常！");
        Integer defaultAclassId=albumClass.getAclassId();
        Assert.isTrue(!(defaultAclassId == aclassId), "默认相册不能删除！");

        //为了控制事务，就不调用AlbumPicService，直接调用AlbumPicMapper
        albumPicMapper.updateToDefaultAlbumClass(aclassId, defaultAclassId);
        return albumClassMapper.deleteByPrimaryKey(aclassId);
    }

    @Override
    public AlbumClass findByAclassIdAndUserId(Integer aclassId, Long userId) {
        return albumClassMapper.findByAclassIdAndUserId(aclassId,userId);
    }

    @Override
    public int update(AlbumClass albumClass, Long userId) {
        Store store=storeService.findByMemberId(userId);
        Integer storeId = store.getStoreId();
        albumClass.setStoreId(storeId);
        return albumClassMapper.updateByStoreId(albumClass);
    }

}
