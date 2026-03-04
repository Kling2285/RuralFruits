package com.example.ruralfruits.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruralfruits.entity.Notice;
import com.example.ruralfruits.mapper.NoticeMapper;
import com.example.ruralfruits.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
@Autowired
    private NoticeMapper noticeMapper;
}
