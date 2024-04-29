
<?xml version="1.0" encoding="UTF-8"?>
<feed xmlns:yt="http://www.youtube.com/xml/schemas/2015" xmlns:media="http://search.yahoo.com/mrss/" xmlns="http://www.w3.org/2005/Atom">
    <link rel="self" href="http://www.youtube.com/feeds/videos.xml?channel_id=${channel.channelId}"/>
    <id>yt:channel:${channel.channelId}</id>
    <yt:channelId>${channel.channelId}</yt:channelId>
    <title>${channel.title}</title>
    <link rel="alternate" href="https://www.youtube.com/channel/${channel.channelId}"/>
    <author>
        <name>YouTube Channel Feeder</name>
        <uri>https://www.youtube.com/channel/${channel.channelId}</uri>
    </author>
    <published>${channel.rssDateUpdated}</published>
    ${videos}
</feed>
