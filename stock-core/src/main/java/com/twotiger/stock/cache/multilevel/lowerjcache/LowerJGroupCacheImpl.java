package com.twotiger.stock.cache.multilevel.lowerjcache;

import com.twotiger.stock.cache.api.impl.jcache.JCacheImpl;
import com.twotiger.stock.cache.exceptions.CacheException;
import com.twotiger.stock.cache.multilevel.LowerCache;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import java.nio.charset.Charset;

/**
 * jgroup jcache
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2015/11/18
 * Time: 0:06
 * To change this template use File | Settings | File Templates.
 */
public class LowerJGroupCacheImpl extends JCacheImpl implements LowerCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(LowerJGroupCacheImpl.class);
    private static final Charset CHARSET = Charset.forName("utf-8");

    private final JChannel channel;

    public LowerJGroupCacheImpl(Cache cache, String jGroupConfig) {
        super(cache);
        try {
            channel = new JChannel(jGroupConfig);
            channel.setReceiver(new RemoveKeyReceiverAdapter());
            channel.connect(name());
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void removeLateral(String key){
        remove(key);
        Message message = new Message(null, key.getBytes(CHARSET));
        try {
            channel.send(message);
        } catch (Exception e) {
            LOGGER.error("send syn message error!key="+key,e);
        }
    }

    @Override
    public String name() {
        return cacheName;
    }

    @Override
    public void destroy() throws Exception {
        try {
            super.destroy();
        }finally {
            channel.close();
        }
    }

    class RemoveKeyReceiverAdapter extends ReceiverAdapter{
        public void receive(Message msg) {
            if (msg.getSrc().equals(channel.getAddress())) {//self
                return;
            }
            try {
                byte[] buffers = msg.getBuffer();
                if (buffers != null && buffers.length > 0) {
                    String key = new String(buffers, CHARSET);
                    LOGGER.debug("syn remove local key={}", key);
                    if (onRemove(key)) {
                        LOGGER.debug("local key exists del success!");
                    }
                }
            }catch (Exception e){
                LOGGER.error("receive message error! msg=" + msg.toString());
            }
        }
    }
}
