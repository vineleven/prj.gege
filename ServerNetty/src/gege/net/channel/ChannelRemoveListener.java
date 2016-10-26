package gege.net.channel;

import io.netty.channel.ChannelId;




public interface ChannelRemoveListener {
	void onRemove( ChannelId id );
}
