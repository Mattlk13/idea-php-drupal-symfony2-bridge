package de.espend.idea.php.drupal.completion;


import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.drupal.utils.DrupalPattern;
import de.espend.idea.php.drupal.utils.TranslationUtil;
import fr.adrienbrault.idea.symfony2plugin.Symfony2ProjectComponent;
import fr.adrienbrault.idea.symfony2plugin.routing.RouteHelper;
import fr.adrienbrault.idea.symfony2plugin.util.PhpElementsUtil;
import org.jetbrains.annotations.NotNull;

public class PhpCompletionContributor extends CompletionContributor {

    public PhpCompletionContributor() {

        // t('foo');
        // @TODO: pattern
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {

                PsiElement psiElement = completionParameters.getOriginalPosition();
                if (psiElement == null || !Symfony2ProjectComponent.isEnabled(psiElement)) {
                    return;
                }

                PsiElement literal = psiElement.getContext();
                if (!(literal instanceof StringLiteralExpression)) {
                    return;
                }

                PsiElement parameterList = literal.getParent();
                if (!(parameterList instanceof ParameterList)) {
                    return;
                }

                PsiElement functionReference = parameterList.getParent();
                if (!(functionReference instanceof FunctionReference) || !"t".equals(((FunctionReference) functionReference).getName())) {
                    return;
                }

                TranslationUtil.attachGetTextLookupKeys(completionResultSet, psiElement.getProject());

            }

        });

        // 'route_name' => 'foo';
        // @TODO: pattern
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {

                PsiElement psiElement = completionParameters.getOriginalPosition();
                if (psiElement == null || !Symfony2ProjectComponent.isEnabled(psiElement) || !DrupalPattern.isAfterArrayKey(psiElement, "route_name")) {
                    return;
                }

                completionResultSet.addAllElements(RouteHelper.getRoutesLookupElements(psiElement.getProject()));

            }

        });

    }
}