<item>
    <title>${video.title}</title>
    <link>${video.transcriptUrl}</link>
    <description>
        <![CDATA[ <img src="${video.thumbnailUrl}" /><p>${video.description}</p>]]>
    </description>
    <media:content url="${video.thumbnailUrl}" type="image/jpeg"/>
    <guid>${video.transcriptUrl}</guid>
    <pubDate>${video.publishedDate}</pubDate>
</item>
