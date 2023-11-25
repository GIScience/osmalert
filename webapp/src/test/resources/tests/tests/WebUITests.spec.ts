import { test } from '@playwright/test';

test('checkCredentials successful', async ({ page}) => {
    await page.goto('http://localhost:8080/login');

    await page.getByLabel('username').fill(process.env.UNAME);

    await page.getByLabel('password').fill(process.env.PASSWORD);

    await page.getByRole('button', { name: 'Sign in' }).click();

    await page.waitForURL('http://localhost:8080');
});

test('checkCredentials failed', async ({ page}) => {
    await page.goto('http://localhost:8080/login');

    await page.getByLabel('username').fill('user123');

    await page.getByLabel('password').fill('1234');

    await page.getByRole('button', { name: 'Sign in' }).click();

    await page.waitForURL('http://localhost:8080/login?error');
});


