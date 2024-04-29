<entry>
    <id>yt:video:M-FResl0kfE</id>
    <yt:videoId>${video.id}</yt:videoId>
    <yt:channelId>${video.channelId}</yt:channelId>
    <title>${video.id}</title>
    <link rel="alternate" href="https://www.youtube.com/watch?v=${video.id}"/>
    <author>
        <name>${channel.channelTitle}</name>
        <uri>https://www.youtube.com/channel/${channel.channelTitle}</uri>
    </author>
    <published>${video.rssDateUpdated}</published>
    <updated>${video.rssDateUpdated}</updated>
    <media:group>
        <media:title>${video.title}</media:title>
        <media:content url="https://www.youtube.com/v/${video.id}?version=3" type="application/x-shockwave-flash" width="640" height="390"/>
        <media:thumbnail url="${video.thumbnailUrl}" width="480" height="360"/>
        <media:description>${video.description}</media:description>
        <media:community>
            <media:starRating count="0" average="0.00" min="1" max="5"/>
            <media:statistics views="2"/>
        </media:community>
    </media:group>
</entry>