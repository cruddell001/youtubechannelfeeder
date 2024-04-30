<item>
    <title>${video.title}</title>
    <link>${video.transcriptUrl}</link>
    <description>
        <![CDATA[
        <img src="${video.thumbnailUrl}" /><p>${video.description}</p>
        <div>
            <p></p><a href="${video.youtubeUrl}" target="_blank"><img src="/images/watch_on_youtube.png" style="width:75px;" /></a></p>
        </div>
        ]]>
    </description>
    <media:content url="${video.thumbnailUrl}" type="image/jpeg"/>
    <guid>${video.transcriptUrl}</guid>
    <pubDate>${video.publishedDate}</pubDate>
</item>
