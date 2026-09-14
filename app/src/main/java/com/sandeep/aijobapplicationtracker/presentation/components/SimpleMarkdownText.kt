package com.sandeep.aijobapplicationtracker.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SimpleMarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val lines = text.split("\n")
    Column(modifier = modifier) {
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) {
                // Add empty space for paragraph break
                Text(text = "", fontSize = 4.sp, modifier = Modifier.padding(bottom = 4.dp))
                continue
            }
            
            val isBullet = trimmed.startsWith("* ") || trimmed.startsWith("- ")
            val content = if (isBullet) trimmed.substring(2).trim() else trimmed
            
            val annotatedString = buildAnnotatedString {
                var currentIndex = 0
                val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
                val matches = boldRegex.findAll(content)
                
                for (match in matches) {
                    val startIndex = match.range.first
                    val endIndex = match.range.last + 1
                    val boldText = match.groupValues[1]
                    
                    // Add text before bold
                    if (currentIndex < startIndex) {
                        append(content.substring(currentIndex, startIndex))
                    }
                    
                    // Add bold text
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF191C1E))) {
                        append(boldText)
                    }
                    
                    currentIndex = endIndex
                }
                
                // Add remaining text
                if (currentIndex < content.length) {
                    append(content.substring(currentIndex))
                }
            }
            
            if (isBullet) {
                androidx.compose.foundation.layout.Row(modifier = Modifier.padding(bottom = 6.dp, start = 8.dp)) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        color = color,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyMedium,
                        color = color,
                        lineHeight = 22.sp
                    )
                }
            } else {
                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}
